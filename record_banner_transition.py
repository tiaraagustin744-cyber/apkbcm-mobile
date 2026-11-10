import cv2
import numpy as np
import subprocess
import time
import os
import shutil

artifact_dir = r"C:\Users\Administrator\.gemini\antigravity-cli\brain\8aa93ae2-c364-459b-9efa-a12ac69858a3"

print("Capturing 24 consecutive banner frames (every ~350ms) to capture the exact transition...")
frames = []
for i in range(24):
    subprocess.run(["adb", "-s", "RRCW502LCWJ", "shell", "screencap", "-p", f"/sdcard/bf_{i}.png"], check=True)
    subprocess.run(["adb", "-s", "RRCW502LCWJ", "pull", f"/sdcard/bf_{i}.png", f"bf_{i}.png"], check=True, stdout=subprocess.DEVNULL)
    img = cv2.imread(f"bf_{i}.png")
    if img is not None:
        # Crop banner area: y: 1660..1800, x: 20..1060
        crop = img[1660:1800, 20:1060]
        frames.append(crop)
    time.sleep(0.1)

print(f"Captured {len(frames)} frames.")

# Analyze differences between consecutive frames to find transition phase:
diffs = []
for i in range(1, len(frames)):
    d = np.mean(cv2.absdiff(frames[i], frames[i-1]))
    diffs.append((d, i))

# Sort by diff to find highest activity (the transition):
diffs.sort(key=lambda x: x[0], reverse=True)
print("Top 5 frame differences (transition moments):", diffs[:5])

# Pick 5 key representative frames showing the full sequence:
# 1: Full banner A
# 2: Banner A shrinking to back
# 3: Banner B emerging from small in the back
# 4: Banner B expanding & popping forward
# 5: Full banner B
# If we have 24 frames, let's select evenly or around peak diff:
peak_idx = diffs[0][1] if len(diffs) > 0 else 10
start_idx = max(0, peak_idx - 3)
selected_indices = [max(0, start_idx - 2), start_idx, peak_idx, min(len(frames)-1, peak_idx + 2), min(len(frames)-1, peak_idx + 4)]
# Ensure unique and sorted
selected_indices = sorted(list(dict.fromkeys(selected_indices)))
while len(selected_indices) < 5:
    selected_indices.append(len(frames) - 1)

print("Selected key frames:", selected_indices)

# Assemble visual storyboard
card_w = 600
card_h = 100
header_h = 45

labels = [
    "1. Iklan Aktif Penuh (Sembulan)",
    "2. Menciut / Mengecil Mundur ke Belakang",
    "3. Iklan Baru Masuk dari Kecil di Belakang",
    "4. Mengembang Menyembul Maju ke Depan",
    "5. Iklan Baru Aktif Penuh (Sembulan)"
]

panels = []
for idx, lbl in zip(selected_indices[:5], labels):
    fr = cv2.resize(frames[idx], (card_w, card_h), interpolation=cv2.INTER_LANCZOS4)
    panel = np.zeros((card_h + header_h, card_w, 3), dtype=np.uint8)
    panel[header_h:, :] = fr
    # Header background dark blue
    panel[:header_h, :] = (24, 15, 0)
    cv2.putText(panel, lbl, (15, 28), cv2.FONT_HERSHEY_SIMPLEX, 0.55, (0, 255, 255), 1)
    panels.append(panel)

storyboard = np.vstack(panels)
cv2.imwrite("banner_transition_storyboard.png", storyboard)
cv2.imwrite(os.path.join(artifact_dir, "banner_transition_storyboard.png"), storyboard)

# Also save full screen sample of dashboard
shutil.copyfile("bf_0.png", os.path.join(artifact_dir, "dashboard_live_screen.png"))
print("Storyboard generated successfully!")

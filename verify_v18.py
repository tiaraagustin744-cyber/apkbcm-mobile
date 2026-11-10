import cv2
import numpy as np
import shutil
import os

live_path = r"F:\Bca Mobile Android\current_screen_live_v18.png"
ref_path = r"F:\Bca Mobile Android\{966AC060-5B43-440F-90C4-57E201DBBD4E}.png"
artifact_dir = r"C:\Users\Administrator\.gemini\antigravity-cli\brain\8aa93ae2-c364-459b-9efa-a12ac69858a3"

if not os.path.exists(live_path):
    print("Live image not found yet:", live_path)
    exit(1)

live = cv2.imread(live_path)
ref = cv2.imread(ref_path)

h_live, w_live = live.shape[:2]
h_ref, w_ref = ref.shape[:2]

print(f"Live shape: {w_live}x{h_live}, Ref shape: {w_ref}x{h_ref}")

# Inspect horizon at x = 0..48 (left margin) and x = 1032..1080 (right margin)
print("Live left margin (x=24, y=1000..1150) color profile:")
for y in range(1040, 1140, 10):
    b, g, r = live[y, 24]
    print(f"y={y}: BGR=({b}, {g}, {r})")

# Crop horizon area around KlikBCA & myBCA: y: 850..1300
crop_live = live[850:1300, :]
crop_ref = ref[850:1300, :]

# Resize ref if needed, but both are 1080x2640 or 1080x2400? Let's check
if crop_ref.shape != crop_live.shape:
    crop_ref = cv2.resize(crop_ref, (crop_live.shape[1], crop_live.shape[0]))

comp_horizon = np.hstack([crop_ref, crop_live])

# Full comparison
scale = 0.5
full_ref_resized = cv2.resize(ref, (int(w_live * scale), int(h_live * scale)))
full_live_resized = cv2.resize(live, (int(w_live * scale), int(h_live * scale)))
comp_full = np.hstack([full_ref_resized, full_live_resized])

# Save comparison files
cv2.imwrite(os.path.join(artifact_dir, "comparison_horizon_v18.png"), comp_horizon)
cv2.imwrite(os.path.join(artifact_dir, "comparison_full_v18.png"), comp_full)
shutil.copyfile(live_path, os.path.join(artifact_dir, "current_screen_live_v18.png"))

print("Comparison v18 generated successfully!")

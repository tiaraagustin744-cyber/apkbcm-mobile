import cv2
import numpy as np
import os
import shutil

ref1_path = r"F:\Bca Mobile Android\{966AC060-5B43-440F-90C4-57E201DBBD4E}.png"
ref2_path = r"F:\Bca Mobile Android\{695B87C6-2BF9-429B-839F-BCDE79FA8F3C}.png"
live_path = r"F:\Bca Mobile Android\current_screen_live_v21.png"
artifact_dir = r"C:\Users\Administrator\.gemini\antigravity-cli\brain\8aa93ae2-c364-459b-9efa-a12ac69858a3"

if not os.path.exists(live_path):
    print("Live image not found yet!")
    exit(1)

live = cv2.imread(live_path)
ref1 = cv2.imread(ref1_path)
ref2 = cv2.imread(ref2_path)

H_l, W_l = live.shape[:2]
H_r1, W_r1 = ref1.shape[:2]
H_r2, W_r2 = ref2.shape[:2]

print(f"Live: {W_l}x{H_l}, Ref1: {W_r1}x{H_r1}, Ref2: {W_r2}x{H_r2}")

# 1. TRIPLE COMPARISON: Star Flare on letter 'o'
# In ref2: flare around (159, 45) -> y: 20..75, x: 130..190
crop_r2 = ref2[20:75, 130:190]
# In ref1: flare around (183, 108) -> y: 80..135, x: 154..214
crop_r1 = ref1[80:135, 154:214]
# In live: flare around (628, 405) -> y: 320..490, x: 550..710
crop_live = live[320:490, 550:710]

disp_h = 360
disp_r2 = cv2.resize(crop_r2, (disp_h, disp_h), interpolation=cv2.INTER_NEAREST)
disp_r1 = cv2.resize(crop_r1, (disp_h, disp_h), interpolation=cv2.INTER_NEAREST)
disp_live = cv2.resize(crop_live, (disp_h, disp_h), interpolation=cv2.INTER_NEAREST)

header_h = 55
p_r2 = np.zeros((disp_h + header_h, disp_h, 3), dtype=np.uint8)
p_r2[header_h:, :] = disp_r2
cv2.putText(p_r2, "REF {695B87...}", (15, 38), cv2.FONT_HERSHEY_SIMPLEX, 0.65, (0, 255, 255), 2)

p_r1 = np.zeros((disp_h + header_h, disp_h, 3), dtype=np.uint8)
p_r1[header_h:, :] = disp_r1
cv2.putText(p_r1, "REF {966AC0...}", (15, 38), cv2.FONT_HERSHEY_SIMPLEX, 0.65, (0, 255, 255), 2)

p_live = np.zeros((disp_h + header_h, disp_h, 3), dtype=np.uint8)
p_live[header_h:, :] = disp_live
cv2.putText(p_live, "LIVE v21 (Z FLIP 3)", (15, 38), cv2.FONT_HERSHEY_SIMPLEX, 0.65, (0, 255, 0), 2)

comp_star_triple = np.hstack([p_r2, p_r1, p_live])
cv2.imwrite(os.path.join(artifact_dir, "comparison_star_triple_v21.png"), comp_star_triple)
cv2.imwrite("comparison_star_triple_v21.png", comp_star_triple)

# 2. FULL SCREEN SIDE-BY-SIDE: Ref1 vs Live v21
scale = 0.4
disp_full_l = cv2.resize(live, (int(W_l * scale), int(H_l * scale)), interpolation=cv2.INTER_LANCZOS4)
disp_full_r = cv2.resize(ref1, (int(W_r1 * (int(H_l * scale) / H_r1)), int(H_l * scale)), interpolation=cv2.INTER_LANCZOS4)

p_full_r = np.zeros((disp_full_r.shape[0] + header_h, disp_full_r.shape[1], 3), dtype=np.uint8)
p_full_r[header_h:, :] = disp_full_r
cv2.putText(p_full_r, "REFERENSI UTAMA", (15, 38), cv2.FONT_HERSHEY_SIMPLEX, 0.75, (0, 255, 255), 2)

p_full_l = np.zeros((disp_full_l.shape[0] + header_h, disp_full_l.shape[1], 3), dtype=np.uint8)
p_full_l[header_h:, :] = disp_full_l
cv2.putText(p_full_l, "LIVE HP GALAXY Z FLIP 3", (15, 38), cv2.FONT_HERSHEY_SIMPLEX, 0.75, (0, 255, 0), 2)

comp_full = np.hstack([p_full_r, p_full_l])
cv2.imwrite(os.path.join(artifact_dir, "comparison_full_v21.png"), comp_full)
cv2.imwrite("comparison_full_v21.png", comp_full)

shutil.copyfile(live_path, os.path.join(artifact_dir, "current_screen_live_v21.png"))
print("All artifacts for v21 generated successfully!")

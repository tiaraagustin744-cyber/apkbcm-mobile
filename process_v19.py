import cv2
import numpy as np
import os
import shutil

ref_horizon_path = r"F:\Bca Mobile Android\{966AC060-5B43-440F-90C4-57E201DBBD4E}.png"
ref_star_path = r"F:\Bca Mobile Android\{695B87C6-2BF9-429B-839F-BCDE79FA8F3C}.png"
live_path = r"F:\Bca Mobile Android\current_screen_live_v19.png"
artifact_dir = r"C:\Users\Administrator\.gemini\antigravity-cli\brain\8aa93ae2-c364-459b-9efa-a12ac69858a3"

if not os.path.exists(live_path):
    print("Live image not found yet!")
    exit(1)

live = cv2.imread(live_path)
ref_h = cv2.imread(ref_horizon_path)
ref_s = cv2.imread(ref_star_path)

H_l, W_l = live.shape[:2]
H_rh, W_rh = ref_h.shape[:2]
H_rs, W_rs = ref_s.shape[:2]

print(f"Live: {W_l}x{H_l}, Ref Horizon: {W_rh}x{H_rh}, Ref Star: {W_rs}x{H_rs}")

# 1. Comparison Horizon (myBCA & KlikBCA row with margins)
# In live, KlikBCA & myBCA row is y: 800..1300
crop_live_h = live[800:1300, :]

# In ref_h, cards m-BCA..Info BCA are y: 145..410. KlikBCA..myBCA is y: 200..350
crop_ref_h = ref_h[200:350, :]

# Resize both to same display height of 500 px
disp_h = 500
aspect_live = crop_live_h.shape[1] / crop_live_h.shape[0]
w_disp_live = int(disp_h * aspect_live)
disp_live_h = cv2.resize(crop_live_h, (w_disp_live, disp_h), interpolation=cv2.INTER_LANCZOS4)

aspect_ref = crop_ref_h.shape[1] / crop_ref_h.shape[0]
w_disp_ref = int(disp_h * aspect_ref)
disp_ref_h = cv2.resize(crop_ref_h, (w_disp_ref, disp_h), interpolation=cv2.INTER_LANCZOS4)

# Create labeled panel
header_h = 60
panel_ref = np.zeros((disp_h + header_h, w_disp_ref, 3), dtype=np.uint8)
panel_ref[header_h:, :] = disp_ref_h
cv2.putText(panel_ref, "REFERENSI {966AC060...}", (15, 40), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 255), 2)

panel_live = np.zeros((disp_h + header_h, w_disp_live, 3), dtype=np.uint8)
panel_live[header_h:, :] = disp_live_h
cv2.putText(panel_live, "HASIL LIVE v19 (TERPASANG DI HP)", (15, 40), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 0), 2)

comp_horizon = np.hstack([panel_ref, panel_live])
cv2.imwrite(os.path.join(artifact_dir, "comparison_horizon_v19.png"), comp_horizon)
cv2.imwrite("comparison_horizon_v19.png", comp_horizon)

# 2. Comparison Full Screen
scale = 0.4
disp_full_live = cv2.resize(live, (int(W_l * scale), int(H_l * scale)), interpolation=cv2.INTER_LANCZOS4)
disp_full_ref = cv2.resize(ref_h, (int(W_rh * (int(H_l * scale) / H_rh)), int(H_l * scale)), interpolation=cv2.INTER_LANCZOS4)

header_full = 60
p_full_ref = np.zeros((disp_full_ref.shape[0] + header_full, disp_full_ref.shape[1], 3), dtype=np.uint8)
p_full_ref[header_full:, :] = disp_full_ref
cv2.putText(p_full_ref, "REFERENSI USER", (15, 40), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 255, 255), 2)

p_full_live = np.zeros((disp_full_live.shape[0] + header_full, disp_full_live.shape[1], 3), dtype=np.uint8)
p_full_live[header_full:, :] = disp_full_live
cv2.putText(p_full_live, "LIVE v19 DI HP FISIK", (15, 40), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 255, 0), 2)

comp_full = np.hstack([p_full_ref, p_full_live])
cv2.imwrite(os.path.join(artifact_dir, "comparison_full_v19.png"), comp_full)
cv2.imwrite("comparison_full_v19.png", comp_full)

# 3. Comparison Star Flare
# Logo area on live: y: 350..650, x: 250..830
crop_live_star = live[350:650, 250:830]
# Star ref: y: 40..250, x: 20..300
crop_ref_star = ref_s[40:250, 20:300]

star_disp_h = 300
w_ls = int(star_disp_h * (crop_live_star.shape[1] / crop_live_star.shape[0]))
w_rs = int(star_disp_h * (crop_ref_star.shape[1] / crop_ref_star.shape[0]))

disp_live_star = cv2.resize(crop_live_star, (w_ls, star_disp_h), interpolation=cv2.INTER_LANCZOS4)
disp_ref_star = cv2.resize(crop_ref_star, (w_rs, star_disp_h), interpolation=cv2.INTER_LANCZOS4)

p_star_ref = np.zeros((star_disp_h + header_h, w_rs, 3), dtype=np.uint8)
p_star_ref[header_h:, :] = disp_ref_star
cv2.putText(p_star_ref, "REF BINTANG {695B87...}", (15, 40), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 255, 255), 2)

p_star_live = np.zeros((star_disp_h + header_h, w_ls, 3), dtype=np.uint8)
p_star_live[header_h:, :] = disp_live_star
cv2.putText(p_star_live, "LIVE BINTANG TIPIS v19", (15, 40), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 255, 0), 2)

comp_star = np.hstack([p_star_ref, p_star_live])
cv2.imwrite(os.path.join(artifact_dir, "comparison_star_v19.png"), comp_star)
cv2.imwrite("comparison_star_v19.png", comp_star)

shutil.copyfile(live_path, os.path.join(artifact_dir, "current_screen_live_v19.png"))
print("All artifacts generated and copied successfully!")

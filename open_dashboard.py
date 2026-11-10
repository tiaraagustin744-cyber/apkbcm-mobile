import subprocess
import time

time.sleep(0.5)
# 1. Tap m-BCA pada card menu
subprocess.run(['adb', '-s', 'RRCW502LCWJ', 'shell', 'input', 'tap', '540', '600'])
time.sleep(1.0)

# 2. Input 6 alphanum kode akses
subprocess.run(['adb', '-s', 'RRCW502LCWJ', 'shell', 'input', 'text', '123456'])
time.sleep(0.5)

# 3. Tap tombol Login pada dialog
subprocess.run(['adb', '-s', 'RRCW502LCWJ', 'shell', 'input', 'tap', '750', '1405'])
time.sleep(1.2)

print('Dashboard opened successfully!')

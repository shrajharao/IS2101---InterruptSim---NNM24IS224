# 🧠 Interrupt Controller Simulation (Java)

This project simulates a simple **Interrupt Controller** system in Java.  
It demonstrates **interrupt handling**, **device masking**, and **priority-based ISR (Interrupt Service Routine)** execution using **multithreading** and **synchronization** concepts.

---

## ⚙️ Features

- Simulates 3 hardware devices:
  - 🖮 **Keyboard** (Priority 1)
  - 🖱️ **Mouse** (Priority 2)
  - 🖨️ **Printer** (Priority 3)
- Each device runs in its own thread and randomly generates interrupts.
- Interrupts are handled based on device **priority** (lower number = higher priority).
- Devices can be **masked (disabled)** or **enabled** dynamically.
- Maintains a synchronized **ISR log** with timestamps.
- Demonstrates `wait()`, `notify()`, and Java synchronization in action.

---

## 🧩 Class Overview

| Class | Description |
|-------|--------------|
| **InterruptController** | Core controller that handles interrupts, masking, and ISR logging. |
| **InterruptEvent** | Represents a raised interrupt from a specific device. |
| **DeviceThread** | Simulates a device that periodically raises interrupts. |
| **Device (enum)** | Defines available devices with priorities and names. |

---

## 🕹️ How It Works

1. **Main program** creates an `InterruptController` instance.
2. Devices (Keyboard, Mouse, Printer) are started as threads.
3. Each device periodically raises interrupts.
4. The `InterruptController`:
   - Queues interrupts.
   - Handles them in order of **priority**.
   - Logs when each ISR is completed.
5. After 8 seconds, the simulation stops and prints the ISR log.

---

## 💻 Example Output


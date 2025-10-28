import java.util.*;
import java.text.SimpleDateFormat;

public class InterruptControllerShort {

    enum Device {
        KEYBOARD(1, "Keyboard"),
        MOUSE(2, "Mouse"),
        PRINTER(3, "Printer");

        public final int priority;
        public final String name;
        Device(int priority, String name) {
            this.priority = priority;
            this.name = name;
        }
    }

    static class InterruptController {
        private final Map<Device, Boolean> maskStatus = new HashMap<>();
        private final PriorityQueue<InterruptEvent> queue;
        private final List<String> log = Collections.synchronizedList(new ArrayList<>());

        InterruptController() {
            for (Device d : Device.values()) maskStatus.put(d, true);
            queue = new PriorityQueue<>(Comparator.comparingInt(e -> e.device.priority));
        }

        public synchronized void maskDevice(Device d, boolean enable) {
            maskStatus.put(d, enable);
            System.out.println(d.name + " " + (enable ? "enabled" : "masked"));
        }

        public synchronized void raiseInterrupt(Device d) {
            if (!maskStatus.get(d)) {
                System.out.println(d.name + " masked");
                return;
            }
            queue.add(new InterruptEvent(d));
            notify();
        }

        public void startHandler() {
            Thread handler = new Thread(() -> {
                while (true) {
                    InterruptEvent e;
                    synchronized (this) {
                        while (queue.isEmpty()) {
                            try { wait(); } catch (InterruptedException ex) { return; }
                        }
                        e = queue.poll();
                    }
                    handleInterrupt(e.device);
                }
            });
            handler.setDaemon(true);
            handler.start();
        }

        private void handleInterrupt(Device d) {
            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
            System.out.println(d.name + " → ISR done");
            log.add(time + " - " + d.name);
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }

        public void showLog() {
            System.out.println("\n=== ISR Log ===");
            log.forEach(System.out::println);
        }
    }

    static class InterruptEvent {
        final Device device;
        InterruptEvent(Device device) { this.device = device; }
    }

    static class DeviceThread extends Thread {
        private final Device device;
        private final InterruptController controller;
        private final Random rand = new Random();

        DeviceThread(Device d, InterruptController c) {
            this.device = d;
            this.controller = c;
        }

        public void run() {
            try {
                while (true) {
                    Thread.sleep(rand.nextInt(2000) + 1000);
                    controller.raiseInterrupt(device);
                }
            } catch (InterruptedException e) { }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        InterruptController ic = new InterruptController();
        ic.startHandler();

        ic.maskDevice(Device.KEYBOARD, true);
        ic.maskDevice(Device.MOUSE, true);
        ic.maskDevice(Device.PRINTER, false);

        DeviceThread t1 = new DeviceThread(Device.KEYBOARD, ic);
        DeviceThread t2 = new DeviceThread(Device.MOUSE, ic);
        DeviceThread t3 = new DeviceThread(Device.PRINTER, ic);

        t1.start(); t2.start(); t3.start();

        Thread.sleep(8000); // run for 8 seconds
        t1.interrupt(); t2.interrupt(); t3.interrupt();

        ic.showLog();
        System.out.println("\nSimulation complete.");
    }
}

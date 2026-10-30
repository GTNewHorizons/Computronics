package pl.asie.computronics.integration.gregtech.gregtech5;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import pl.asie.computronics.Computronics;

// For direct callbacks, this wraps a method to be called on the next server tick, so machine states
// do not change while the machine ticks
public class ServerThread {

    public static final ServerThread INSTANCE = new ServerThread();

    private final Queue<Runnable> pending = new ConcurrentLinkedQueue<Runnable>();

    private ServerThread() {}

    public static void schedule(Runnable task) {
        INSTANCE.pending.add(task);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.START) {
            return;
        }
        Runnable task;
        while ((task = pending.poll()) != null) {
            try {
                task.run();
            } catch (Throwable t) {
                Computronics.log.warn("Error in scheduled tick action.", t);
            }
        }
    }
}

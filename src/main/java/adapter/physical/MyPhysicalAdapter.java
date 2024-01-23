package adapter.physical;

import it.wldt.adapter.physical.*;
import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.exception.EventBusException;
import java.util.List;
import java.util.Random;

public class MyPhysicalAdapter extends PhysicalAdapter {

    private final static String LUMINOSITY_PROPERTY_KEY = "luminosity-property-key";
    private final static String TURN_OFF_ACTION_KEY = "turn-off-action-key";
    private final static String TURN_ON_ACTION_KEY = "turn-on-action-key";
    private final static String POWER_ON_EVENT_KEY = "power-on-event-key";
    private final static String POWER_OFF_EVENT_KEY = "power-off-event-key";

    private boolean STOPPED = false;

    public MyPhysicalAdapter(String id) {
        super(id);
    }

    @Override
    public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalAssetActionWldtEvent) {
        if (physicalAssetActionWldtEvent != null) {
            switch (physicalAssetActionWldtEvent.getActionKey()) {
                case (TURN_OFF_ACTION_KEY):
                    System.out.println("[LampPhysicalAdapter] Received Turn Off Action -> " + physicalAssetActionWldtEvent.getBody());
                    break;
                case (TURN_ON_ACTION_KEY):
                    System.out.println("[LampPhysicalAdapter] Received Turn On Action -> " + physicalAssetActionWldtEvent.getBody());
                    break;
                default:
                    System.out.println("[LampPhysicalAdapter] Unable to manage incoming Physical Action!");
                }
        }
    }

    @Override
    public void onAdapterStart() {
        publishPhysicalAssetDescription();
        new Thread(physicalAssetEmulation()).start();
    }

    @Override
    public void onAdapterStop() {
        this.STOPPED = true;
    }

    private void publishPhysicalAssetDescription() {
        PhysicalAssetDescription pad = new PhysicalAssetDescription();
        PhysicalAssetProperty<Double> luminosityProperty = new PhysicalAssetProperty<Double>(LUMINOSITY_PROPERTY_KEY, 0.0);
        PhysicalAssetEvent powerOnEvent = new PhysicalAssetEvent(POWER_ON_EVENT_KEY, "text/plain");
        PhysicalAssetEvent powerOffEvent = new PhysicalAssetEvent(POWER_OFF_EVENT_KEY, "text/plain");
        PhysicalAssetAction turnOnAction = new PhysicalAssetAction(TURN_ON_ACTION_KEY,"text/plain","text/plain");
        PhysicalAssetAction turnOffAction = new PhysicalAssetAction(TURN_OFF_ACTION_KEY,"text/plain","text/plain");
        pad.setProperties(List.of(luminosityProperty));
        pad.setEvents(List.of(powerOnEvent, powerOffEvent));
        pad.setActions(List.of(turnOnAction, turnOffAction));
        try {
            this.notifyPhysicalAdapterBound(pad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable physicalAssetEmulation() {
        return () -> {
            try {
                Thread.sleep(10000);
                while (!STOPPED) {
                    PhysicalAssetPropertyWldtEvent<Double> luminosityUpdate = new PhysicalAssetPropertyWldtEvent<>(
                            LUMINOSITY_PROPERTY_KEY,
                            new Random().nextDouble()
                            );
                    publishPhysicalAssetPropertyWldtEvent(luminosityUpdate);
                    Thread.sleep(5000);
                }
            } catch (EventBusException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }
}

package adapter.digital;

import it.wldt.adapter.digital.DigitalAdapter;
import it.wldt.core.state.*;
import it.wldt.exception.EventBusException;
import it.wldt.exception.WldtDigitalTwinStateEventException;

import java.util.Random;
import java.util.stream.Collectors;

public class MyDigitalAdapter extends DigitalAdapter<Void> {

    private boolean STOPPED = false;

    public MyDigitalAdapter(String id) {
        super(id, true);
    }

    @Override
    protected void onStateChangePropertyCreated(DigitalTwinStateProperty digitalTwinStateProperty) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangePropertyCreated(): " + digitalTwinStateProperty);
    }

    @Override
    protected void onStateChangePropertyUpdated(DigitalTwinStateProperty digitalTwinStateProperty) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangePropertyUpdated(): " + digitalTwinStateProperty);
    }

    @Override
    protected void onStateChangePropertyDeleted(DigitalTwinStateProperty digitalTwinStateProperty) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangePropertyDeleted(): " + digitalTwinStateProperty);
    }

    @Override
    protected void onStatePropertyUpdated(DigitalTwinStateProperty digitalTwinStateProperty) {
        System.out.println("[DemoDigitalAdapter] -> onStatePropertyUpdated(): " + digitalTwinStateProperty);
    }

    @Override
    protected void onStatePropertyDeleted(DigitalTwinStateProperty digitalTwinStateProperty) {
        System.out.println("[DemoDigitalAdapter] -> onStatePropertyDeleted(): " + digitalTwinStateProperty);
    }

    @Override
    protected void onStateChangeActionEnabled(DigitalTwinStateAction digitalTwinStateAction) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangeActionEnabled(): " + digitalTwinStateAction);
    }

    @Override
    protected void onStateChangeActionUpdated(DigitalTwinStateAction digitalTwinStateAction) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangeActionUpdated(): " + digitalTwinStateAction);
    }

    @Override
    protected void onStateChangeActionDisabled(DigitalTwinStateAction digitalTwinStateAction) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangeActionDisabled(): " + digitalTwinStateAction);
    }

    @Override
    protected void onStateChangeEventRegistered(DigitalTwinStateEvent digitalTwinStateEvent) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangeEventRegistered(): " + digitalTwinStateEvent);
    }

    @Override
    protected void onStateChangeEventRegistrationUpdated(DigitalTwinStateEvent digitalTwinStateEvent) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangeEventRegistrationUpdated(): " + digitalTwinStateEvent);
    }

    @Override
    protected void onStateChangeEventUnregistered(DigitalTwinStateEvent digitalTwinStateEvent) {
        System.out.println("[DemoDigitalAdapter] -> onStateChangeEventUnregistered(): " + digitalTwinStateEvent);
    }

    @Override
    public void onAdapterStart() {
        System.out.println("[TestDigitalAdapter] -> onAdapterStart()");
    }

    @Override
    public void onAdapterStop() {
        this.STOPPED = true;
        System.out.println("[DemoDigitalAdapter] -> onAdapterStop()");
    }

    @Override
    public void onDigitalTwinSync(IDigitalTwinState iDigitalTwinState) {
        System.out.println("[DemoDigitalAdapter] -> onDigitalTwinSync(): " + iDigitalTwinState);
        try {
            digitalTwinState.getEventList()
                    .map(eventList -> eventList.stream()
                            .map(DigitalTwinStateEvent::getKey)
                            .collect(Collectors.toList()))
                    .ifPresent(eventKeys -> {
                        try {
                            observeDigitalTwinEventsNotifications(eventKeys);
                        } catch (EventBusException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (WldtDigitalTwinStateEventException e) {
            e.printStackTrace();
        }
        new Thread(emulateIncomingDigitalAction()).start();
    }

    @Override
    public void onDigitalTwinUnSync(IDigitalTwinState iDigitalTwinState) {
        System.out.println("[LampDigitalAdapter] -> onDigitalTwinUnSync(): " + iDigitalTwinState);
    }

    @Override
    public void onDigitalTwinCreate() {
        System.out.println("[LampDigitalAdapter] -> onDigitalTwinCreate()");
    }

    @Override
    public void onDigitalTwinStart() {
        System.out.println("[LampDigitalAdapter] -> onDigitalTwinStart()");
    }

    @Override
    public void onDigitalTwinStop() {
        System.out.println("[LampDigitalAdapter] -> onDigitalTwinStop()");
    }

    @Override
    public void onDigitalTwinDestroy() {
        System.out.println("[LampDigitalAdapter] -> onDigitalTwinDestroy()");
    }

    @Override
    protected void onStateChangeRelationshipInstanceDeleted(DigitalTwinStateRelationshipInstance digitalTwinStateRelationshipInstance) {
        System.out.println("[LampDigitalAdapter] -> onStateChangeRelationshipInstanceDeleted(): " + digitalTwinStateRelationshipInstance);
    }

    @Override
    protected void onStateChangeRelationshipDeleted(DigitalTwinStateRelationship digitalTwinStateRelationship) {
        System.out.println("[LampDigitalAdapter] -> onStateChangeRelationshipDeleted(): " + digitalTwinStateRelationship);
    }

    @Override
    protected void onStateChangeRelationshipInstanceCreated(DigitalTwinStateRelationshipInstance digitalTwinStateRelationshipInstance) {
        System.out.println("[LampDigitalAdapter] -> onStateChangeRelationshipInstanceCreated(): " + digitalTwinStateRelationshipInstance);
    }

    @Override
    protected void onStateChangeRelationshipCreated(DigitalTwinStateRelationship digitalTwinStateRelationship) {
        System.out.println("[LampDigitalAdapter] -> onStateChangeRelationshipCreated(): " + digitalTwinStateRelationship);
    }

    @Override
    protected void onDigitalTwinStateEventNotificationReceived(DigitalTwinStateEventNotification digitalTwinStateEventNotification) {
        System.out.println("[LampDigitalAdapter] -> onDigitalTwinStateEventNotificationReceived(): " + digitalTwinStateEventNotification);
    }

    private Runnable emulateIncomingDigitalAction(){
        return () -> {
            try {
                Thread.sleep(15000);
                Random random = new Random();

                while (!STOPPED) {
                    if (random.nextBoolean()) {
                        publishDigitalActionWldtEvent("power-on-action-key","power-on");
                    } else {
                        publishDigitalActionWldtEvent("power-off-action-key","power-off");
                    }
                    Thread.sleep(3000);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}

package shadowing;

import it.wldt.adapter.digital.event.DigitalActionWldtEvent;
import it.wldt.adapter.physical.PhysicalAssetDescription;
import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceCreatedWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceDeletedWldtEvent;
import it.wldt.core.model.ShadowingModelFunction;
import it.wldt.core.state.*;
import it.wldt.exception.EventBusException;

import java.util.Map;

public class MyShadowingFunction extends ShadowingModelFunction {

    public MyShadowingFunction(String id) {
        super(id);
    }

    @Override
    protected void onCreate() {}

    @Override
    protected void onStart() {}

    @Override
    protected void onStop() {}

    @Override
    protected void onDigitalTwinBound(Map<String, PhysicalAssetDescription> physicalAssetDescriptionMap) {
        System.out.println("[LampShadowingFunction] -> onDigitalTwinBound(): " + physicalAssetDescriptionMap);
        physicalAssetDescriptionMap.values().forEach(pad -> {
            pad.getProperties().forEach(property -> {
                try {
                    this.digitalTwinState.createProperty(new DigitalTwinStateProperty<>(property.getKey(),(Double) property.getInitialValue()));
                    //Start observing the variation of the physical property in order to receive notifications
                    //Without this call the Shadowing Function will not receive any notifications or callback about
                    //incoming physical property of the target type and with the target key
                    this.observePhysicalAssetProperty(property);
                    System.out.println("[LampShadowingFunction] -> onDigitalTwinBound() -> Property Created & Observed:" + property.getKey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            pad.getEvents().forEach(event -> {
                try {
                    //Instantiate a new DT State Event with the same key and type
                    DigitalTwinStateEvent dtStateEvent = new DigitalTwinStateEvent(event.getKey(), event.getType());
                    //Create and write the event on the DT's State
                    this.digitalTwinState.registerEvent(dtStateEvent);
                    //Start observing the variation of the physical event in order to receive notifications
                    //Without this call the Shadowing Function will not receive any notifications or callback about
                    //incoming physical events of the target type and with the target key
                    this.observePhysicalAssetEvent(event);
                    System.out.println("[LampShadowingFunction] -> onDigitalTwinBound() -> Event Created & Observed:" + event.getKey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            pad.getActions().forEach(action -> {
                try {
                    //Instantiate a new DT State Action with the same key and type
                    DigitalTwinStateAction dtStateAction = new DigitalTwinStateAction(action.getKey(), action.getType(), action.getContentType());
                    //Enable the action on the DT's State
                    this.digitalTwinState.enableAction(dtStateAction);
                    System.out.println("[LampShadowingFunction] -> onDigitalTwinBound() -> Action Enabled:" + action.getKey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            pad.getRelationships().forEach(relationship -> {
                try{
                    if(relationship != null && relationship.getName().equals("insideIn")){
                        DigitalTwinStateRelationship<String> insideInDtStateRelationship = new DigitalTwinStateRelationship<>(relationship.getName(), relationship.getName());
                        this.digitalTwinState.createRelationship(insideInDtStateRelationship);
                        observePhysicalAssetRelationship(relationship);
                        System.out.println("[LampShadowingFunction] -> onDigitalTwinBound() -> Relationship Created & Observed :" + relationship.getName());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        });

        try {
            //Start observation to receive all incoming Digital Action through active Digital Adapter
            //Without this call the Shadowing Function will not receive any notifications or callback about
            //incoming request to execute an exposed DT's Action
            observeDigitalActionEvents();

            //Notify the DT Core that the Bounding phase has been correctly completed and the DT has evaluated its
            //internal status according to what is available and declared through the Physical Adapters
            notifyShadowingSync();
        } catch (EventBusException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onDigitalTwinUnBound(Map<String, PhysicalAssetDescription> map, String s) {}

    @Override
    protected void onPhysicalAdapterBidingUpdate(String s, PhysicalAssetDescription physicalAssetDescription) {}

    @Override
    protected void onPhysicalAssetPropertyVariation(PhysicalAssetPropertyWldtEvent<?> physicalAssetPropertyWldtEvent) {
        try {

            System.out.println("[LampShadowingFunction] -> onPhysicalAssetPropertyVariation() -> Variation on Property :" + physicalAssetPropertyWldtEvent.getPhysicalPropertyId());

            this.digitalTwinState.updateProperty(new DigitalTwinStateProperty<>(
                    physicalAssetPropertyWldtEvent.getPhysicalPropertyId(),
                    physicalAssetPropertyWldtEvent.getBody()));

            System.out.println("[LampShadowingFunction] -> onPhysicalAssetPropertyVariation() -> DT State UPDATE Property :" + physicalAssetPropertyWldtEvent.getPhysicalPropertyId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPhysicalAssetEventNotification(PhysicalAssetEventWldtEvent<?> physicalAssetEventWldtEvent) {
        try {

            System.out.println("[LampShadowingFunction] -> onPhysicalAssetPropertyVariation() -> Notification for Event :" + physicalAssetEventWldtEvent.getPhysicalEventKey());

            this.digitalTwinState.notifyDigitalTwinStateEvent(new DigitalTwinStateEventNotification<>(
                    physicalAssetEventWldtEvent.getPhysicalEventKey(),
                    physicalAssetEventWldtEvent.getBody(),
                    physicalAssetEventWldtEvent.getCreationTimestamp()));

            System.out.println("[LampShadowingFunction] -> onPhysicalAssetPropertyVariation() -> DT State Notification for Event:" + physicalAssetEventWldtEvent.getPhysicalEventKey());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPhysicalAssetRelationshipEstablished(PhysicalAssetRelationshipInstanceCreatedWldtEvent<?> physicalAssetRelationshipInstanceCreatedWldtEvent) {}

    @Override
    protected void onPhysicalAssetRelationshipDeleted(PhysicalAssetRelationshipInstanceDeletedWldtEvent<?> physicalAssetRelationshipInstanceDeletedWldtEvent) {}

    @Override
    protected void onDigitalActionEvent(DigitalActionWldtEvent<?> digitalActionWldtEvent) {
        try {
            this.publishPhysicalAssetActionWldtEvent(digitalActionWldtEvent.getActionKey(), digitalActionWldtEvent.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

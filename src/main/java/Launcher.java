import adapter.digital.MyDigitalAdapter;
import adapter.physical.MyPhysicalAdapter;
import it.wldt.core.engine.WldtEngine;
import shadowing.MyShadowingFunction;

public class Launcher {

    public static void main(String[] args) {
        try {
            WldtEngine engine = new WldtEngine(new MyShadowingFunction("lamp-shadowing-function"), "lamp-1");
            engine.addPhysicalAdapter(new MyPhysicalAdapter("lamp-physical-adapter"));
            engine.addDigitalAdapter(new MyDigitalAdapter("lamp-digital-adapter"));
            engine.startLifeCycle();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

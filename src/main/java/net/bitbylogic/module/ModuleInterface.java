package net.bitbylogic.module;

public interface ModuleInterface {

    void onRegister();

    void onEnable();

    void onReload();

    void onDisable();

    ModuleData getModuleData();

}

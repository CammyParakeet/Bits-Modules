package net.bitbylogic.module;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class ModuleCommand {

    private BitsModule module;

    public <O extends BitsModule> O getModuleAs(Class<O> moduleClass) {
        return (O) module;
    }

}

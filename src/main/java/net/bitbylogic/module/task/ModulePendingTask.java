package net.bitbylogic.module.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.bitbylogic.module.BitsModule;

@Getter
@RequiredArgsConstructor
public abstract class ModulePendingTask<T extends BitsModule> {

    private final Class<T> clazz;

    public abstract void accept(T module);

}

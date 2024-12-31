package net.bitbylogic.module.command;

import net.bitbylogic.module.BitsModule;
import net.bitbylogic.module.ModuleManager;
import net.bitbylogic.utils.message.format.Formatter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Command({"module", "mdl", "modules", "mdls"})
@CommandPermission("bitsmodules.command")
public class ModulesCommand {

    @Dependency
    private ModuleManager moduleManager;

    @CommandPlaceholder
    public void onDefault(CommandSender sender) {
        sender.sendMessage(
                Formatter.listHeader("Module Commands", ""),
                Formatter.command("module list <page>", "List all modules."),
                Formatter.command("module reload <id>", "Reload the specified module's config."),
                Formatter.command("module enable <id>", "Enable the specified module."),
                Formatter.command("module disable <id>", "Disable the specified module."),
                Formatter.command("module toggle <id>", "Toggles the specified module.")
        );
    }

    @Subcommand("list")
    @CommandPermission("bitsmodules.command.list")
    public void onList(CommandSender sender, @Default("1") int page) {
        displayPage(sender, page);
    }

    @Subcommand("reload")
    @CommandPermission("bitsmodules.command.reload")
    public void onReload(CommandSender sender, BitsModule module) {
        if (module == null) {
            sender.sendMessage(Formatter.error("Modules", "Invalid module."));
            return;
        }

        if (!module.isEnabled()) {
            sender.sendMessage(Formatter.error("Modules", "That module isn't enabled."));
        }

        sender.sendMessage(Formatter.success("Modules", String.format("Reloading module! <c#separator>(</c><c#success_secondary>Name</c><c#separator>:</c> %s<c#separator>,</c> <c#success_secondary>ID</c><c#separator>:</c> %s<c#separator>)</c>", module.getModuleData().getName(), module.getModuleData().getId())));
        module.reloadConfig();
        module.loadConfigPaths();
        module.onReload();
    }

    @Subcommand("enable")
    @CommandPermission("bitsmodules.command.enable")
    public void onEnable(CommandSender sender, BitsModule module) {
        if (module == null) {
            sender.sendMessage(Formatter.error("Modules", "Invalid module."));
            return;
        }

        if (module.isEnabled()) {
            sender.sendMessage(Formatter.error("Modules", "That module isn't disabled."));
            return;
        }

        sender.sendMessage(Formatter.success("Modules", "Enabling module! <c#separator>(</c><c#success_secondary>Name</c><c#separator>:</c> %s<c#separator>,</c> <c#success_secondary>ID</c><c#separator>:</c> %s<c#separator>)</c>", module.getModuleData().getName(), module.getModuleData().getId()));
        moduleManager.enableModule(module.getModuleData().getId());
    }

    @Subcommand("disable")
    @CommandPermission("bitsmodules.command.disable")
    public void onDisable(CommandSender sender, BitsModule module) {
        if (module == null) {
            sender.sendMessage(Formatter.error("Modules", "Invalid module."));
            return;
        }

        if (!module.isEnabled()) {
            sender.sendMessage(Formatter.error("Modules", "That module isn't enabled."));
            return;
        }

        sender.sendMessage(Formatter.success("Modules", "Disabling module! <c#separator>(</c><c#success_secondary>Name</c><c#separator>:</c> %s<c#separator>,</c> <c#success_secondary>ID</c><c#separator>:</c> %s<c#separator>)</c>", module.getModuleData().getName(), module.getModuleData().getId()));
        moduleManager.disableModule(module.getModuleData().getId());
    }

    @Subcommand("debug")
    @CommandPermission("bitsmodules.command.debug")
    public void onDebug(CommandSender sender, BitsModule module) {
        if (module == null) {
            sender.sendMessage(Formatter.error("Modules", "Invalid module."));
            return;
        }

        if (!module.isDebug()) {
            module.setDebug(true);
            sender.sendMessage(Formatter.success("Modules", "Enabling debug for module! <c#separator>(</c><c#success_secondary>Name</c><c#separator>:</c> %s<c#separator>,</c> <c#success_secondary>ID</c><c#separator>:</c> %s<c#separator>)</c>", module.getModuleData().getName(), module.getModuleData().getId()));
            return;
        }

        module.setDebug(false);
        sender.sendMessage(Formatter.success("Modules", "Disabling debug for module! <c#separator>(</c><c#success_secondary>Name</c><c#separator>:</c> %s<c#separator>,</c> <c#success_secondary>ID</c><c#separator>:</c> %s<c#separator>)</c>", module.getModuleData().getName(), module.getModuleData().getId()));
    }

    @Subcommand("toggle")
    @CommandPermission("bitsmodules.command.toggle")
    public void onToggle(CommandSender sender, BitsModule module) {
        if (module == null) {
            sender.sendMessage(Formatter.error("Modules", "Invalid module."));
            return;
        }

        if (module.isEnabled()) {
            sender.sendMessage(Formatter.success("Modules", "Disabling module! <c#separator>(</c><c#success_secondary>Name</c><c#separator>:</c> %s<c#separator>,</c> <c#success_secondary>ID</c><c#separator>:</c> %s<c#separator>)</c>", module.getModuleData().getName(), module.getModuleData().getId()));
            moduleManager.disableModule(module.getModuleData().getId());
            return;
        }

        sender.sendMessage(Formatter.success("Modules", "Enabling module! <c#separator>(</c><c#success_secondary>Name</c><c#separator>:</c> <c#success_highlight>%s</c><c#separator>,</c> <c#success_secondary>ID</c><c#separator>:</c> <c#succes_highlight>%s</c><c#separator>)</c>", module.getModuleData().getName(), module.getModuleData().getId()));
        moduleManager.enableModule(module.getModuleData().getId());
    }

    @Subcommand("tasks")
    @CommandPermission("bitsmodules.command.tasks")
    public void onTasks(CommandSender sender, BitsModule module, @Default("1") int page) {
        if (module == null) {
            sender.sendMessage(Formatter.error("Modules", "Invalid module."));
            return;
        }

        if (module.getTasks().isEmpty()) {
            sender.sendMessage(Formatter.error("Module", module.getModuleData().getName() + " has no active tasks."));
            return;
        }

        List<String> lines = new ArrayList<>();
        module.getTasks().forEach(task -> {
            if (!task.isActive()) {
                return;
            }

            lines.add(Formatter.listItem(task.getId(), task.getType().name()));
        });

        sender.sendMessage(Formatter.getPagedList(module.getModuleData().getName() + "'s Tasks", lines, page));
    }

    private void displayPage(CommandSender sender, int page) {
        List<BitsModule> modules = new ArrayList<>(moduleManager.getModules().values());
        int pages = modules.size() / 10.0d % 1 == 0 ? modules.size() / 10 : modules.size() / 10 + 1;
        int lastPossibleModule = modules.size();

        if (page == 0 || page > pages) {
            sender.sendMessage(Formatter.error("Modules", "Invalid page&8: &f%s", page));
            return;
        }

        int startingModule = (page * 10) - 10;
        int lastModule = Math.min(startingModule + 10, lastPossibleModule);

        sender.sendMessage(Formatter.format("<c#separator>&m     </c>&r <c#separator>(</c> <c#primary>&lMODULE LIST </c><c#separator>)&m     </c>"));

        for (int i = startingModule; i < lastModule; i++) {
            BitsModule module = modules.get(i);

            BaseComponent moduleComponent = Formatter.richFormat(
                    "<c#separator>-</c> <c#primary>%s</c> <c#separator>(</c><c#secondary>ID</c><c#separator>:</c> <c#highlight>%s</c><c#separator>,</c> <c#secondary>Status</c><c#separator>:</c> <c#highlight>%s</c><c#separator>)</c>",
                    module.getModuleData().getName(),
                    module.getModuleData().getId(),
                    Formatter.format(module.isEnabled() ? "<c#success_highlight>Enabled</c>" : "<c#error_highlight>Disabled</c>"));

            moduleComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Formatter.richFormat("<c#highlight>%s</c>", module.getModuleData().getDescription()))));
            moduleComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/module toggle " + module.getModuleData().getId()));

            sender.spigot().sendMessage(moduleComponent);
        }

        sender.sendMessage(Formatter.replace("<c#separator>&m        </c>&r <c#separator>(</c> <c#secondary>Page</c><c#separator>:</c> <c#highlight>%s</c><c#separator>/</c><c#highlight>%s</c> <c#separator>)&m        </c>", page, pages));
    }

}

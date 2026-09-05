package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TranslationTextComponent;

public class PublishCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.publish.failed"));
    private static final DynamicCommandExceptionType ALREADY_PUBLISHED_EXCEPTION = new DynamicCommandExceptionType((p_208900_0_) -> {
        return new TranslationTextComponent("commands.publish.alreadyPublished", p_208900_0_);
    });

    public static void register(CommandDispatcher<CommandSource> dispatcher) {

    }

    private static int publish(CommandSource source, int port) throws CommandSyntaxException {
        if (source.getServer().getPublic()) {
            throw ALREADY_PUBLISHED_EXCEPTION.create(source.getServer().getServerPort());
        } else if (!source.getServer().shareToLAN(source.getServer().getGameType(), false, port)) {
            throw FAILED_EXCEPTION.create();
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.publish.success", port), true);
            return port;
        }
    }
}

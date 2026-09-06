package net.p3pp3rf1y.sophisticatedbackpacks.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackAccessLogger;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BackpackUUIDArgumentType implements ArgumentType<EaglercraftUUID> {
	private static final SimpleCommandExceptionType INVALID_UUID = new SimpleCommandExceptionType(new TranslationTextComponent("argument.uuid.invalid"));

	@Override
	public EaglercraftUUID parse(StringReader reader) throws CommandSyntaxException {
		int start = reader.getCursor();
		while (reader.canRead() && isUuidPart(reader.peek())) {
			reader.skip();
		}
		String text = reader.getString().substring(start, reader.getCursor());
		try {
			return EaglercraftUUID.fromString(text);
		}
		catch (IllegalArgumentException e) {
			reader.setCursor(start);
			throw INVALID_UUID.createWithContext(reader);
		}
	}

	private static boolean isUuidPart(char c) {
		return c == '-' || (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		if (context.getSource() instanceof CommandSource) {
			return ISuggestionProvider.suggest(BackpackAccessLogger.getBackpackUuids().stream().map(EaglercraftUUID::toString).collect(Collectors.toList()), builder);
		} else if (context.getSource() instanceof ISuggestionProvider) {
			ISuggestionProvider isuggestionprovider = (ISuggestionProvider) context.getSource();
			return isuggestionprovider.getSuggestionsFromServer((CommandContext<ISuggestionProvider>) context, builder);
		}
		return Suggestions.empty();
	}

	public static BackpackUUIDArgumentType backpackUuid() {
		return new BackpackUUIDArgumentType();
	}

	@Override
	public Collection<String> getExamples() {
		return Collections.singleton("dd12be42-52a9-4a91-a8a1-11c01849e498");
	}
}

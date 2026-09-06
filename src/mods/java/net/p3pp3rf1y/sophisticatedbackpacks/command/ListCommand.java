package net.p3pp3rf1y.sophisticatedbackpacks.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.AccessLogRecord;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackAccessLogger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ListCommand {
	private ListCommand() {}

	@SuppressWarnings("java:S1452")
	static ArgumentBuilder<CommandSource, ?> register() {
		return Commands.literal("list")
				.executes(context -> printBackpackList(new ArrayList<>(BackpackAccessLogger.getAllBackpackLogs()), context.getSource()))
				.then(Commands.argument("playerName", BackpackPlayerArgumentType.playerName())
						.executes(context -> printBackpackList(new ArrayList<>(BackpackAccessLogger.getBackpackLogsForPlayer(context.getArgument("playerName", String.class))), context.getSource()))
				);
	}

	private static int printBackpackList(List<AccessLogRecord> allLogs, CommandSource source) {
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		allLogs.sort(Comparator.comparing(AccessLogRecord::getAccessTime).reversed());
		source.sendFeedback(new TranslationTextComponent("commands.sophisticatedbackpacks.list.header"), false);
		allLogs.forEach(alr -> {
			ITextComponent message = new StringTextComponent("");
			message.appendSibling(new StringTextComponent(alr.getBackpackName())
					.applyTextStyle(s ->
							s.setColor(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/sophisticatedbackpacks give @p " + alr.getBackpackUuid()))
									.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.sophisticatedbackpacks.backpack_uuid.tooltip", alr.getBackpackUuid())))
					)
			);
			message.appendSibling(new StringTextComponent(", "));
			message.appendSibling(new TranslationTextComponent("commands.sophisticatedbackpacks.list.cloth_color").applyTextStyle(nearestFormatting(alr.getClothColor())));
			message.appendSibling(new StringTextComponent(" "));
			message.appendSibling(new TranslationTextComponent("commands.sophisticatedbackpacks.list.trim_color").applyTextStyle(nearestFormatting(alr.getTrimColor())));
			message.appendSibling(new StringTextComponent(", "));
			message.appendSibling(new StringTextComponent(alr.getPlayerName()));
			message.appendSibling(new StringTextComponent(", "));
			message.appendSibling(new StringTextComponent(dateFormat.format(new Date(alr.getAccessTime()))));
			source.sendFeedback(message, false);
		});
		return 0;
	}

	private static TextFormatting nearestFormatting(int rgb) {
		int red = rgb >> 16 & 255;
		int green = rgb >> 8 & 255;
		int blue = rgb & 255;
		TextFormatting nearest = TextFormatting.WHITE;
		int nearestDistance = Integer.MAX_VALUE;
		for (TextFormatting formatting : TextFormatting.values()) {
			Integer color = formatting.getColor();
			if (color == null) {
				continue;
			}
			int dr = red - (color >> 16 & 255);
			int dg = green - (color >> 8 & 255);
			int db = blue - (color & 255);
			int distance = dr * dr + dg * dg + db * db;
			if (distance < nearestDistance) {
				nearestDistance = distance;
				nearest = formatting;
			}
		}
		return nearest;
	}
}

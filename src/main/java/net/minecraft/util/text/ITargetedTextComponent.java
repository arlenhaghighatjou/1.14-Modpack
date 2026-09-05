package net.minecraft.util.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;

public interface ITargetedTextComponent {
   ITextComponent createNames( CommandSource p_197668_1_,  Entity p_197668_2_, int p_197668_3_) throws CommandSyntaxException;
}

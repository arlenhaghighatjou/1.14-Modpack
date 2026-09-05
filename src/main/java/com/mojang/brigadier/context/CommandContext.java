package com.mojang.brigadier.context;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.tree.CommandNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandContext<S> {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = new HashMap();

    static {
        PRIMITIVE_TO_WRAPPER.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_TO_WRAPPER.put(Byte.TYPE, Byte.class);
        PRIMITIVE_TO_WRAPPER.put(Short.TYPE, Short.class);
        PRIMITIVE_TO_WRAPPER.put(Character.TYPE, Character.class);
        PRIMITIVE_TO_WRAPPER.put(Integer.TYPE, Integer.class);
        PRIMITIVE_TO_WRAPPER.put(Long.TYPE, Long.class);
        PRIMITIVE_TO_WRAPPER.put(Float.TYPE, Float.class);
        PRIMITIVE_TO_WRAPPER.put(Double.TYPE, Double.class);
    }

    private final S source;
    private final String input;
    private final Command<S> command;
    private final Map<String, ParsedArgument<S, ?>> arguments;
    private final CommandNode<S> rootNode;
    private final List<ParsedCommandNode<S>> nodes;
    private final StringRange range;
    private final CommandContext<S> child;
    private final RedirectModifier<S> modifier;
    private final boolean forks;

    public CommandContext(S source, String input, Map<String, ParsedArgument<S, ?>> arguments, Command<S> command, CommandNode<S> rootNode, List<ParsedCommandNode<S>> nodes, StringRange range, CommandContext<S> child, RedirectModifier<S> modifier, boolean forks) {
        this.source = source;
        this.input = input;
        this.arguments = arguments;
        this.command = command;
        this.rootNode = rootNode;
        this.nodes = nodes;
        this.range = range;
        this.child = child;
        this.modifier = modifier;
        this.forks = forks;
    }

    public CommandContext<S> copyFor(S source) {
        return this.source == source ? this : new CommandContext(source, this.input, this.arguments, this.command, this.rootNode, this.nodes, this.range, this.child, this.modifier, this.forks);
    }

    public CommandContext<S> getChild() {
        return this.child;
    }

    public CommandContext<S> getLastChild() {
        CommandContext result;
        for (result = this; result.getChild() != null; result = result.getChild()) {
        }

        return result;
    }

    public Command<S> getCommand() {
        return this.command;
    }

    public S getSource() {
        return this.source;
    }

    public <V> V getArgument(String name, Class<V> clazz) {
        ParsedArgument<S, ?> argument = (ParsedArgument) this.arguments.get(name);
        if (argument == null) {
            throw new IllegalArgumentException("No such argument '" + name + "' exists on this command");
        } else {
            Object result = argument.getResult();
            if (((Class) PRIMITIVE_TO_WRAPPER.getOrDefault(clazz, clazz)).isAssignableFrom(result.getClass())) {
                return (V) result;
            } else {
                throw new IllegalArgumentException("Argument '" + name + "' is defined as " + result.getClass().getSimpleName() + ", not " + clazz);
            }
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof CommandContext)) {
            return false;
        } else {
            CommandContext that = (CommandContext) o;
            if (!this.arguments.equals(that.arguments)) {
                return false;
            } else if (!this.rootNode.equals(that.rootNode)) {
                return false;
            } else if (this.nodes.size() == that.nodes.size() && this.nodes.equals(that.nodes)) {
                if (this.command != null) {
                    if (!this.command.equals(that.command)) {
                        return false;
                    }
                } else if (that.command != null) {
                    return false;
                }

                if (!this.source.equals(that.source)) {
                    return false;
                } else {
                    if (this.child != null) {
                        if (!this.child.equals(that.child)) {
                            return false;
                        }
                    } else if (that.child != null) {
                        return false;
                    }

                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public int hashCode() {
        int result = this.source.hashCode();
        result = 31 * result + this.arguments.hashCode();
        result = 31 * result + (this.command != null ? this.command.hashCode() : 0);
        result = 31 * result + this.rootNode.hashCode();
        result = 31 * result + this.nodes.hashCode();
        result = 31 * result + (this.child != null ? this.child.hashCode() : 0);
        return result;
    }

    public RedirectModifier<S> getRedirectModifier() {
        return this.modifier;
    }

    public StringRange getRange() {
        return this.range;
    }

    public String getInput() {
        return this.input;
    }

    public CommandNode<S> getRootNode() {
        return this.rootNode;
    }

    public List<ParsedCommandNode<S>> getNodes() {
        return this.nodes;
    }

    public boolean hasNodes() {
        return !this.nodes.isEmpty();
    }

    public boolean isForked() {
        return this.forks;
    }
}

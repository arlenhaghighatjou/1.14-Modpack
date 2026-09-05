package net.lax1dude.eaglercraft.internal.vfs2;

import net.lax1dude.eaglercraft.internal.VFSFilenameIterator;

// Backends match by raw string prefix, so iterating "New World" also yields siblings like
// "New World (1)". Forward only the base path itself and entries genuinely beneath it.
class VFSChildFilterIterator implements VFSFilenameIterator {

    private final String base;
    private final String prefix;
    private final VFSFilenameIterator delegate;

    VFSChildFilterIterator(String base, VFSFilenameIterator delegate) {
        this.base = base;
        this.prefix = base + VFile2.pathSeperator;
        this.delegate = delegate;
    }

    @Override
    public void next(String entry) {
        if (entry != null && (entry.equals(base) || entry.startsWith(prefix))) {
            delegate.next(entry);
        }
    }

    @Override
    public void end() {
        delegate.end();
    }

}

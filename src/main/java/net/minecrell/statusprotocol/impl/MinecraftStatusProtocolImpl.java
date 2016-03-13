/*
 * StatusProtocol
 * Copyright (c) 2016, Minecrell <https://github.com/Minecrell>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.minecrell.statusprotocol.impl;

import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.spongepowered.api.MinecraftVersion;

import java.util.Iterator;

public enum MinecraftStatusProtocolImpl {
    v1_9("1.9", "1_9", 1, 9),
    v1_8_9("1.8.9", "1_8_9", 1, 8, 9);

    private static final char VERSION_SEPARATOR = '.';
    private static final Splitter VERSION_SPLITTER = Splitter.on(VERSION_SEPARATOR);
    private static final char VERSION_CLASSIFIER = '-';

    private static final String IMPL_CLASS = "StatusProtocolImpl";

    private static final MinecraftStatusProtocolImpl[] implementations = values();
    public static final MinecraftStatusProtocolImpl LATEST = implementations[0];

    private final String target;
    private final String className;
    private final int major;
    private final int version;
    private final int minor;

    MinecraftStatusProtocolImpl(String target, String classSuffix, int major, int version) {
        this(target, classSuffix, major, version, 0);
    }

    MinecraftStatusProtocolImpl(String target, String classSuffix, int major, int version, int minor) {
        this.target = target;
        this.className = MinecraftStatusProtocolImpl.class.getPackage().getName() + '.' + IMPL_CLASS + '_' + classSuffix;
        this.major = major;
        this.version = version;
        this.minor = minor;
    }

    public String getTarget() {
        return this.target;
    }

    public StatusProtocolImpl construct() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> clazz = Class.forName(this.className);
        return (StatusProtocolImpl) clazz.newInstance();
    }

    public static MinecraftStatusProtocolImpl find(MinecraftVersion minecraftVersion, Logger logger) {
        String version = minecraftVersion.getName();
        if (version.indexOf(VERSION_SEPARATOR) == -1) {
            logger.warn("Unsupported Minecraft version format '{}'. Falling back to latest version (for Minecraft {})", version, LATEST.target);
            return LATEST;
        }

        // Strip trailing dashes (for pre-release versions)
        int[] parts = new int[3];
        Iterator<String> itr = VERSION_SPLITTER.split(removeEnd(version, VERSION_CLASSIFIER)).iterator();
        int i = 0;
        while (itr.hasNext() && i < 3) {
            try {
                parts[i++] = Integer.parseInt(itr.next());
            } catch (NumberFormatException e) {
                parts[i-1] = Integer.MAX_VALUE;
                logger.warn("Failed to parse Minecraft version '{}' at index {}");
            }
        }

        for (MinecraftStatusProtocolImpl impl : implementations) {
            if (parts[0] >= impl.major && parts[1] >= impl.version && parts[2] >= impl.minor) {
                return impl;
            }
        }

        logger.warn("No matching implementation available for your Minecraft version '{}'. The plugin will be disabled.", version);
        return null;
    }

    private static String removeEnd(String s, char c) {
        int pos = s.lastIndexOf(c);
        return pos != -1 ? s.substring(0, pos) : s;
    }

}

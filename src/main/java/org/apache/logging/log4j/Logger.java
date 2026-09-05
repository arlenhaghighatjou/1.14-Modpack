/*
 * Copyright (c) 2022-2024 lax1dude. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.apache.logging.log4j;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.PlatformRuntime;

public class Logger {

    public final String loggerName;

    Logger(String name) {
        this.loggerName = name;
    }

    public void trace(String msg) {
        log(Level.TRACE, msg);
    }

    public void trace(String msg, Object... args) {
        log(Level.TRACE, msg, args);
    }

    public void trace(Throwable msg) {
        log(Level.WARN, msg);
    }

    public void trace(String msg, Throwable t) {
        logExcp(Level.TRACE, msg, t);
    }

    public void debug(String msg) {
        log(Level.DEBUG, msg);
    }

    public void debug(String msg, Object... args) {
        log(Level.DEBUG, msg, args);
    }

    public void debug(Throwable msg) {
        log(Level.DEBUG, msg);
    }

    public void debug(String msg, Throwable t) {
        logExcp(Level.DEBUG, msg, t);
    }

    public void info(String msg) {
        log(Level.INFO, msg);
    }

    public void info(String msg, Object... args) {
        log(Level.INFO, msg, args);
    }

    public void info(Throwable msg) {
        log(Level.INFO, msg);
    }

    public void info(String msg, Throwable t) {
        logExcp(Level.INFO, msg, t);
    }

    public void warn(String msg) {
        log(Level.WARN, msg);
    }

    public void warn(String msg, Object... args) {
        log(Level.WARN, msg, args);
    }

    public void warn(Throwable msg) {
        log(Level.WARN, msg);
    }

    public void warn(String msg, Throwable t) {
        logExcp(Level.WARN, msg, t);
    }

    public void error(String msg) {
        log(Level.ERROR, msg);
    }

    public void error(String msg, Object... args) {
        log(Level.ERROR, msg, args);
    }

    public void error(Throwable msg) {
        log(Level.ERROR, msg);
    }

    public void error(String msg, Throwable t) {
        logExcp(Level.ERROR, msg, t);
    }

    public void fatal(String msg) {
        log(Level.FATAL, msg);
    }

    public void fatal(String msg, Object... args) {
        log(Level.FATAL, msg, args);
    }

    public void fatal(Throwable msg) {
        log(Level.FATAL, msg);
    }

    public void fatal(String msg, Throwable t) {
        logExcp(Level.FATAL, msg, t);
    }

    public void log(Level level, String msg) {
        if (level.levelInt >= LogManager.logLevel.levelInt) {
            synchronized (LogManager.logLock) {
                String line = "[" + formatTimestamp(System.currentTimeMillis()) + "]" +
                        "[" + EagRuntime.currentThreadName() + "/" + level.levelName + "]" +
                        "[" + loggerName + "]: " + msg;
                level.getPrintStream().println(line);
                if (LogManager.logRedirector != null) {
                    LogManager.logRedirector.log(line, level.isErr);
                }
            }
        }
    }

    public void log(Level level, String msg, Object... args) {
        if (level.levelInt >= LogManager.logLevel.levelInt) {
            Throwable trailing = null;
            if (args.length > 0 && args[args.length - 1] instanceof Throwable
                    && countPlaceholders(msg) < args.length) {
                trailing = (Throwable) args[args.length - 1];
                Object[] shortened = new Object[args.length - 1];
                System.arraycopy(args, 0, shortened, 0, shortened.length);
                args = shortened;
            }
            synchronized (LogManager.logLock) {
                String line = "[" + formatTimestamp(System.currentTimeMillis()) + "]" +
                        "[" + EagRuntime.currentThreadName() + "/" + level.levelName + "]" +
                        "[" + loggerName + "]: " + formatParams(msg, args);
                level.getPrintStream().println(line);
                if (LogManager.logRedirector != null) {
                    LogManager.logRedirector.log(line, level.isErr);
                }
            }
            if (trailing != null) {
                logExcp(level, "Exception Thrown", trailing);
            }
        }
    }

    private static String formatTimestamp(long timeMillis) {
        int timeOfDay = (int)(timeMillis % 86400000L);
        if (timeOfDay < 0) {
            timeOfDay += 86400000;
        }
        int millis = timeOfDay % 1000;
        int totalSeconds = timeOfDay / 1000;
        int seconds = totalSeconds % 60;
        int totalMinutes = totalSeconds / 60;
        int minutes = totalMinutes % 60;
        int hours = totalMinutes / 60;
        hours %= 12;
        if (hours == 0) {
            hours = 12;
        }

        StringBuilder builder = new StringBuilder(12);
        appendTwoDigits(builder, hours);
        builder.append(':');
        appendTwoDigits(builder, minutes);
        builder.append(':');
        appendTwoDigits(builder, seconds);
        builder.append('+');
        if (millis < 100) {
            builder.append('0');
        }
        if (millis < 10) {
            builder.append('0');
        }
        builder.append(millis);
        return builder.toString();
    }

    private static void appendTwoDigits(StringBuilder builder, int value) {
        if (value < 10) {
            builder.append('0');
        }
        builder.append(value);
    }

    private static int countPlaceholders(String msg) {
        int count = 0;
        int idx = 0;
        while ((idx = msg.indexOf("{}", idx)) != -1) {
            ++count;
            idx += 2;
        }
        return count;
    }

    public static String formatParams(String msg, Object... args) {
        if (args.length > 0) {
            StringBuilder builtString = new StringBuilder();
            for (int i = 0; i < args.length; ++i) {
                int idx = msg.indexOf("{}");
                if (idx != -1) {
                    builtString.append(msg.substring(0, idx));
                    builtString.append(args[i]);
                    msg = msg.substring(idx + 2);
                } else {
                    break;
                }
            }
            builtString.append(msg);
            return builtString.toString();
        } else {
            return msg;
        }
    }

    public void log(Level level, Throwable msg) {
        logExcp(level, "Exception Thrown", msg);
    }

    private void logExcp(final Level level, String h, Throwable msg) {
        if (msg == null) {
            log(level, "{}: <null>", h);
            return;
        }
        log(level, "{}: {}", h, msg.toString());
        EagRuntime.getStackTrace(msg, (e) -> log(level, "    at {}", e));
        PlatformRuntime.printJSExceptionIfBrowser(msg);
        Throwable cause = msg.getCause();
        if (cause != null) {
            logExcp(level, "Caused By", cause);
        }
    }

    public boolean isDebugEnabled() {
        return LogManager.logLevel.levelInt <= Level.DEBUG.levelInt;
    }

}

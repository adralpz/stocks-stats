import kleur from "kleur";

interface LoggerOptions {
  margin: number;
  leftMargin: number;
  topMargin: number;
  rightMargin: number;
  bottomMargin: number;
  prefix: string;
}

const defaultOptions: LoggerOptions = {
  margin: 0,
  leftMargin: 0,
  topMargin: 0,
  rightMargin: 0,
  bottomMargin: 0,
  prefix: "  ",
};

function message(message?: string, options: Partial<LoggerOptions> = defaultOptions): void {
  const mergedOptions: LoggerOptions = { ...defaultOptions, ...options };

  if (mergedOptions?.topMargin) {
    for (let i = 0; i < mergedOptions.topMargin; i++) {
      console.log();
    }
  }

  if (!message) {
    console.log();
    return;
  }

  console.log(`${" ".repeat(mergedOptions.leftMargin)}${mergedOptions.prefix}${message}${" ".repeat(mergedOptions.rightMargin)}`);

  if (mergedOptions?.bottomMargin) {
    for (let i = 0; i < mergedOptions.bottomMargin; i++) {
      console.log();
    }
  }
}

function debug(message: any, ...args: any[]): void {
  const isDebugging = process.argv.includes("--debug") || process.argv.includes("-d") || process.env.NODE_ENV === "development";
  if (!isDebugging) return;

  const isObject = typeof message === "object";
  const formattedMessage = isObject ? JSON.stringify(message) : message;
  console.debug(`${kleur.yellow().bold("  [DEBUG]")} ${kleur.yellow(formattedMessage)}`, ...args);
}

function info(message: string, ...args: any[]): void {
  console.info(kleur.yellow(`[INFO] ${message}`), ...args);
}

function success(message: string, ...args: any[]): void {
  console.info(kleur.green(`${message}`), ...args);
}

function warn(message: string, ...args: any[]): void {
  console.warn(kleur.yellow(`[!] ${message}`), ...args);
}

function fatal(message: string, ...args: any[]): void {
  console.error(kleur.red(`[FATAL] ${message}`), ...args);
  process.exit(1);
}

function error(message: string, ...args: any[]): void {
  console.error(kleur.red(`[ERROR] ${message}`), ...args);
}

export const Logger = { message, debug, info, success, warn, fatal, error };

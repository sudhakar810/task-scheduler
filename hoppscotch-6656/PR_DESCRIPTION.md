## Issue comment

I'd like to work on this. The cause is in `packages/hoppscotch-cli/src/index.ts`. The `writeErr` override calls `program.help()`, which exits with code 0. Because of `exitOverride()`, that becomes a thrown `CommanderError` with exit code 0, and the empty `catch {}` in `cli()` swallows it. So `hopp`, `hopp test`, `hopp invalid-arg`, `hopp test coll.json --unknown` and similar invocations all print `INVALID_ARGUMENT` but exit with 0. I have a fix with tests ready and will open a PR shortly.

---

## PR title

fix(cli): exit with non-zero code on invalid usage

## PR description

Closes #6656

Invalid CLI usage, such as a missing argument, an unknown command or option, or too many arguments, prints an `INVALID_ARGUMENT` error but exits with code `0`. CI pipelines therefore treat a misconfigured `hopp` invocation as a success.

**Cause:** the `writeErr` output override calls `program.help()`. That function exits with code `0`, which `exitOverride()` turns into a thrown `CommanderError` with `exitCode: 0`. The empty `catch {}` in `cli()` swallows it, and the original usage error, with exit code `1`, is never raised.

### What's changed

- [x] `writeErr` now prints the help text with `program.outputHelp()`, which doesn't exit. Commander calls `writeErr` once per help chunk, so a guard makes sure the help prints only once. The output is byte-for-byte the same as before.
- [x] `cli()` copies the exit code of any `CommanderError` to `process.exitCode`, so usage errors exit with `1` and `--help` / `--ver` still exit with `0`.
- [x] Added e2e tests for the non-zero exit code on invalid usage (no args, `test` with no file, unknown command, unknown option, extra argument) and for exit code `0` on `--help`, `--ver` and `test --help`. Also added an exit-code assertion to the existing `--env` with no file test.

| Invocation | Before | After |
| --- | --- | --- |
| `hopp` | 0 | 1 |
| `hopp test` | 0 | 1 |
| `hopp invalid-arg` | 0 | 1 |
| `hopp test coll.json --bogus` | 0 | 1 |
| `hopp test a.json b.json` | 0 | 1 |
| `hopp --help` / `--ver` / `test --help` | 0 | 0 |
| `hopp test missing.json` | 1 | 1 |

### Notes to reviewers

- `npx vitest run src/__tests__/e2e/commands/test.spec.ts -t "Argument parsing"`: 7 passed. Without the `index.ts` change, the new invalid-usage test and the updated `--env` test both fail.
- `tsc --noEmit` and prettier pass for `packages/hoppscotch-cli`.
- stdout/stderr content is unchanged. Only the exit code differs.

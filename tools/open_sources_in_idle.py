from pathlib import Path
import subprocess
import sys


PROJECT_ROOT = Path(__file__).resolve().parents[1]
SOURCE_PATTERNS = (
    "src/main/java/**/*.java",
    "src/main/resources/**/*.toml",
    "src/main/resources/**/*.json",
    "build.gradle",
    "gradle.properties",
    "settings.gradle",
)


def collect_files() -> list[str]:
    files: list[Path] = []
    for pattern in SOURCE_PATTERNS:
        files.extend(PROJECT_ROOT.glob(pattern))
    return [str(path) for path in sorted(files) if path.is_file()]


def main() -> int:
    files = collect_files()
    if not files:
        print(f"No source files found under {PROJECT_ROOT}", file=sys.stderr)
        return 1

    subprocess.Popen([sys.executable, "-m", "idlelib", *files], cwd=PROJECT_ROOT)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

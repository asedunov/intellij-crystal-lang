# Changelog

## [0.10] - 2023-10-21

### New features

#### Parser
- [Support parsing of arbitrarily nested block parameters](https://github.com/asedunov/intellij-crystal-lang/issues/66)

#### Editor
- [Support highlighting of escape sequences in heredocs](https://github.com/asedunov/intellij-crystal-lang/issues/71)
- [Highlight heredoc start id when caret is on the end id, and vice versa](https://github.com/asedunov/intellij-crystal-lang/issues/68)
- [Highlight heredoc content similar to string literals](https://github.com/asedunov/intellij-crystal-lang/issues/67)

#### Analysis
- [Report error on duplicating splats inside nested block parameters](https://github.com/asedunov/intellij-crystal-lang/issues/73)
- [Report an error when heredoc indents are too small](https://github.com/asedunov/intellij-crystal-lang/issues/69)
- [Report void expressions used in wrong context](https://github.com/asedunov/intellij-crystal-lang/issues/16)
- [Report branch duplication in case-expressions](https://github.com/asedunov/intellij-crystal-lang/issues/14)
- [Report invalid splat types](https://github.com/asedunov/intellij-crystal-lang/issues/13)

### Fixes

#### Parser
- [Allow string interpolation at the beginning of heredoc](https://github.com/asedunov/intellij-crystal-lang/issues/72)
- [Fix parsing of multiple heredocs](https://github.com/asedunov/intellij-crystal-lang/issues/70)
- [Allow parsing name with "=" in qualified proc literals](https://github.com/asedunov/intellij-crystal-lang/issues/65)

#### Analysis
- [Report "Wrong number of tuple elements" on all branches of case-expression](https://github.com/asedunov/intellij-crystal-lang/issues/62)

#### Structure View
- [Improve rendering of unnamed parameters](https://github.com/asedunov/intellij-crystal-lang/issues/74)
- [Support expressions used as type arguments](https://github.com/asedunov/intellij-crystal-lang/issues/64)
- [Show external names of global variables in a library](https://github.com/asedunov/intellij-crystal-lang/issues/63)

#### Configuration
- [Fix exception on project opening (access file index from run configuration)](https://github.com/asedunov/intellij-crystal-lang/issues/60)

## [0.9] - 2023-07-30

### New features

#### General
- [Crystal 1.9 support](https://github.com/asedunov/intellij-crystal-lang/issues/61)

#### Analysis

- [When macro call is not resolved exactly, resolve it to possible candidate macros](https://github.com/asedunov/intellij-crystal-lang/issues/59)

#### Parameter Info

- [Support parameter info for macro calls](https://github.com/asedunov/intellij-crystal-lang/issues/57)

#### Completion

- [Support reference completion for macros](https://github.com/asedunov/intellij-crystal-lang/issues/56)

## [0.8] - 2023-05-12

### New features

#### General
- [Crystal 1.8 support](https://github.com/asedunov/intellij-crystal-lang/issues/55)

#### Execution
- [Allow user to specify working directory in Crystal run configurations](https://github.com/asedunov/intellij-crystal-lang/issues/53)

#### Build
- [Add run configuration for project build](https://github.com/asedunov/intellij-crystal-lang/issues/52)
- [Track compilation progress in Build toolwindow](https://github.com/asedunov/intellij-crystal-lang/issues/54)

## [0.7] - 2023-03-23

### New features

#### Completion
- [Key completion for shard.yml](https://github.com/asedunov/intellij-crystal-lang/issues/26)

#### Inspections
- [Report when required keys are missing in shard.yml](https://github.com/asedunov/intellij-crystal-lang/issues/39)
- [Report unknown keys in shard.yml](https://github.com/asedunov/intellij-crystal-lang/issues/39)
- [Report unresolved file references in shard.yml](https://github.com/asedunov/intellij-crystal-lang/issues/41)

#### Navigation
- [Support file references in shard.yml](https://github.com/asedunov/intellij-crystal-lang/issues/40)
- [Support URL references in shard.yml](https://github.com/asedunov/intellij-crystal-lang/issues/42)

#### Editor
- [Add line markers for partial definitions](https://github.com/asedunov/intellij-crystal-lang/issues/43)

#### Configuration
- [Implement Crystal project wizard](https://github.com/asedunov/intellij-crystal-lang/issues/27)

## [0.6.1] - 2023-02-11

### New features

### Fixes

- [Fix compatibility with IntelliJ IDEA EAP (231.6890.12)](https://github.com/asedunov/intellij-crystal-lang/issues/38)
- [Add new icons according to IDEA "New UI" style](https://github.com/asedunov/intellij-crystal-lang/issues/37)

## [0.6] - 2023-02-05

### New features

- [Crystal 1.7 support](https://github.com/asedunov/intellij-crystal-lang/issues/32)
- New quick fixes:
  - [Upgrade language level when some feature is available in newer version](https://github.com/asedunov/intellij-crystal-lang/issues/33)
  - [Add missing space in type restrictions](https://github.com/asedunov/intellij-crystal-lang/issues/34)
  - [Convert named tuples with constructor type into hash literals](https://github.com/asedunov/intellij-crystal-lang/issues/35)
  - [Remove call arguments and parameters at improper positions](https://github.com/asedunov/intellij-crystal-lang/issues/36)

## [0.5.5] - 2023-01-19

### New features

- [Support auto-detection of Windows compiler](https://github.com/asedunov/intellij-crystal-lang/issues/31) 

### Fixes

- [Fix compiler validity check on Windows](https://github.com/asedunov/intellij-crystal-lang/issues/29)

## [0.5] - 2022-11-23

### New features

- [Crystal 1.6 support](https://github.com/asedunov/intellij-crystal-lang/issues/25)
- [Basic keyword completion](https://github.com/asedunov/intellij-crystal-lang/issues/8)
- [Type/constant reference completion](https://github.com/asedunov/intellij-crystal-lang/issues/9)
- [External formatter support](https://github.com/asedunov/intellij-crystal-lang/issues/10)

## [0.4] - 2022-10-22

### New features

- [Initial support of macro call resolution](https://github.com/asedunov/intellij-crystal-lang/issues/21)
- Macro override line markers

### Fixes

- [Fix resolve of top-level include/extend](https://github.com/asedunov/intellij-crystal-lang/issues/24)

## [0.3] - 2022-08-04

### New features

- Crystal 1.4/1.5 support
- Find Usages (types and constants)
- Rename refactoring (types and constants)
- More error highlighting

## [0.2.4] - 2022-05-27

### New features

- Initial support of type reference resolution
- Line markers for inheriting and including types
- Type hierarchy view
- "Missing main file" inspection

### Fixes

- Fixes and improvements in parser

## [0.2.3] - 2022-03-27

### New features

- Run configuration for Crystal files
- "New Crystal File" action

### Fixes

- Fixes and improvements in parser

## [0.2.2] - 2022-02-20

### New features

- Standard library support:
  - Project configuration
  - Auto-detection of language version
  - Resolution of library files in `require` expressions

## [0.2.1] - 2022-01-18

### New features

- Crystal 1.3 support

## [0.2] - 2021-12-22

### New features

- `require` support:
  - File reference resolve and completion
  - Highlighting of unresolved files/directories
  - Find usages/rename of files/directories

## [0.1.2] - 2021-11-18

### New features

- Crystal 1.2 support

## [0.1.1] - 2021-11-08

### New features

- Auto-indent on Enter
- Auto-insertion of `end` on Enter
- Basic formatter support

## [0.1] - 2021-09-12

### New features

- Parser
- Basic syntax highlighting
- File structure view
- Go to Class/Go to Symbol navigation
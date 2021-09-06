# W3C DOM Test Suite for eXist-db

![Java CI](https://github.com/eXist-db/exist-domts/workflows/Java%20CI/badge.svg)

To execute:

```bash
$ mvn clean test
```

or if you want a HTML report of the test results:

```bash
$ mvn clean site
```

The report can then be found in `target/site/surefire-report.html`.

# Results

## eXist-db 5.3.0

| Test Suite                | Tests | Errors | Failures | Skipped | Success Rate | Time (s) |
|---------------------------|-------|--------|----------|---------|--------------|----------|
| Level 1 Core (Memtree)    | 527   | 266    | 110      | 0       | 28.653%      | 0.441    |
| Level 2 Core (Memtree)    | 282   | 139    | 69       | 0       | 26.241%      | 0.524    |
| Level 3 Core (Memtree)    | 722   | 593    | 64       | 0       | 9.003%       | 0.558    |
| Level 1 Core (Persistent) | 527   | 272    | 64       | 0       | 36.243%      | 1.482    |
| Level 2 Core (Persistent) | 282   | 115    | 81       | 0       | 30.496%      | 1.233    |
| Level 3 Core (Persistent) | 722   | 624    | 28       | 0       | 9.695%       | 1.336    |

## eXist-db 4.7.0

| Test Suite                | Tests | Errors | Failures | Skipped | Success Rate | Time (s) |
|---------------------------|-------|--------|----------|---------|--------------|----------|
| Level 1 Core (Memtree)    | 527   | 266    | 110      | 0       | 28.653%      | 0.877    |
| Level 2 Core (Memtree)    | 282   | 139    | 69       | 0       | 26.241%      | 0.373    |
| Level 3 Core (Memtree)    | 722   | 597    | 61       | 0       | 8.864%       | 2.600    |
| Level 1 Core (Persistent) | 527   | 272    | 64       | 0       | 36.243%      | 6.901    |
| Level 2 Core (Persistent) | 282   | 115    | 81       | 0       | 30.496%      | 2.578    |
| Level 3 Core (Persistent) | 722   | 624    | 28       | 0       | 9.695%       | 3.382    |

## eXist-db 3.6.1

| Test Suite                | Tests | Errors | Failures | Skipped | Success Rate | Time (s) |
|---------------------------|-------|--------|----------|---------|--------------|----------|
| Level 1 Core (Memtree)    | 527   | 266    | 110      | 0       | 28.653%      | 1.115    |
| Level 2 Core (Memtree)    | 282   | 163    | 69       | 0       | 26.241%      | 0.456    |
| Level 3 Core (Memtree)    | 722   | 604    | 54       | 0       | 8.864%       | 0.861    |
| Level 1 Core (Persistent) | 527   | 279    | 61       | 0       | 35.484%      | 2.206    |
| Level 2 Core (Persistent) | 282   | 116    | 81       | 0       | 30.142%      | 0.817    |
| Level 3 Core (Persistent) | 722   | 624    | 28       | 0       | 9.695%       | 1.407    |

## eXist-db 3.6.0

| Test Suite                | Tests | Errors | Failures | Skipped | Success Rate | Time (s) |
|---------------------------|-------|--------|----------|---------|--------------|----------|
| Level 1 Core (Memtree)    | 527   | 266    | 110      | 0       | 28.653%      | 1.115    |
| Level 2 Core (Memtree)    | 282   | 163    | 69       | 0       | 26.241%      | 0.456    |
| Level 3 Core (Memtree)    | 722   | 604    | 54       | 0       | 8.864%       | 0.861    |
| Level 1 Core (Persistent) | 527   | 279    | 61       | 0       | 35.484%      | 2.206    |
| Level 2 Core (Persistent) | 282   | 116    | 81       | 0       | 30.142%      | 0.817    |
| Level 3 Core (Persistent) | 722   | 624    | 28       | 0       | 9.695%       | 1.407    |

## eXist-db 3.3.0

| Test Suite                | Tests | Errors | Failures	| Skipped | Success Rate | Time (s) |
|---------------------------|-------|--------|----------|---------|--------------|----------|
| Level 1 Core (Memtree)    | 513   | 193    | 192      | 0       | 24.951%      | 1.674    |
| Level 2 Core (Memtree)    | 282   | 143    | 83       | 0       | 19.858%      | 0.921    |
| Level 3 Core (Memtree)    | 717   | 539    | 130      | 0       | 6.695%       | 1.301    |
| Level 1 Core (Persistent) | 513   | 331    | 88       | 0       | 18.324%      | 3.125    |
| Level 2 Core (Persistent) | 282   | 131    | 108      | 0       | 15.248%      | 0.949    |
| Level 3 Core (Persistent) | 717   | 631    | 22       | 0       | 8.926%       | 2.078    |

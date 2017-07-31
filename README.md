# W3C DOM Test Suite for eXist-db

To execute:

```bash
$ mvn clean test
```

or if you want a HTML report of the test results:

```bash
$ mvn clean site
```

The report can then be found in `target/site/target/site/surefire-report.html`.

# Results

## eXist-db 3.3.0

| Test Suite                | Tests | Errors | Failures	| Skipped | Success Rate | Time (s) |
|---------------------------|-------|--------|----------|---------|--------------|----------|
| Level 1 Core (Memtree)    | 513   | 193    | 192      | 0       | 24.951%      | 1.674    |
| Level 2 Core (Memtree)    | 282   | 143    | 83       | 0       | 19.858%      | 0.921    |
| Level 3 Core (Memtree)    | 717   | 539    | 130      | 0       | 6.695%       | 1.301    |
| Level 1 Core (Persistent) | 513   | 331    | 88       | 0       | 18.324%      | 3.125    |
| Level 2 Core (Persistent) | 282   | 131    | 108      | 0       | 15.248%      | 0.949    |
| Level 3 Core (Persistent) | 717   | 631    | 22       | 0       | 8.926%       | 2.078    |

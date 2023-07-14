# arc-reactor-scala
Scala lightweight dependency injection framework

### Install required tools

You need to download and install sbt for this application to run.

```bash
brew install coursier/formulas/coursier && cs setup
```

Once you have sbt installed, the following at the command prompt will start up Play in development mode:

### Running in local
```bash
sbt run
```
Play will start up on the HTTP port at <http://localhost:9000/>.   You don't need to deploy or reload anything -- changing any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request.

### Test

```bash
sbt test
```

### Create Docker image

```bash
./docker-build.sh
```

### Format the specific file only

```bash
sbt clean reload compile scalafmtOnly <file-name>
```

### Format all the files (It is not recommended)

```bash
sbt clean reload compile scalafmtAll
```

### Format all the sbt configuration files

```bash
sbt clean reload compile scalafmtSbt
```

### Apply scalafix rules (scalafix can be removed as it is applied on the compilation time)

```bash
sbt clean reload compile scalafix
```
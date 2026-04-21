#!/bin/bash

GRAAL_HOME=$JAVA_HOME
UPDATE_REACHABILITY_METADATA=false
KOTLINC_BINARY_NAME=kotlinc-native
KOTLINC_DIST_DIR=dist
KOTLINC_BINARY_DIST_DIR=$KOTLINC_DIST_DIR/$KOTLINC_BINARY_NAME

for arg in "$@"; do
  case $arg in
    --graalHome=*)
      GRAAL_HOME="${arg#*=}"
      ;;
    --updateReachabilityMetadata)
      UPDATE_REACHABILITY_METADATA=true
      ;;
    *)
      echo "Unknown argument: $arg"
      echo "Usage: $0 [--graalHome=PATH] [--updateReachabilityMetadata]"
      exit 1
      ;;
  esac
done

NATIVE_IMAGE_BIN=$GRAAL_HOME/bin/native-image

echo 'Starting native image build of the compiler'
echo "native-image path: $NATIVE_IMAGE_BIN"

echo '--- Building kotlin compiler dist ---'
./gradlew -q :dist

echo '--- Building kotlin compiler embeddable ---'
./gradlew -q :kotlin-compiler-embeddable:embeddable

EMBEDDABLE_JAR=$(find prepare/compiler-embeddable -name 'kotlin-compiler-embeddable-*.jar')
STDLIB_JAR=$(find dist/kotlinc/ -name 'kotlin-stdlib.jar')
REFLECT_JAR=$(find dist/kotlinc/ -name 'kotlin-reflect.jar')
COROUTINES_JAR=$(find dist/kotlinc/ -name 'kotlinx-coroutines-core-jvm.jar')
ANNOTATIONS_JAR=$(find dist/kotlinc/ -name 'annotations-*.jar')

CLASSPATH=$EMBEDDABLE_JAR:$STDLIB_JAR:$REFLECT_JAR:$COROUTINES_JAR:$ANNOTATIONS_JAR
echo "Class path: $CLASSPATH"

if [ "$UPDATE_REACHABILITY_METADATA" = true ]; then
  echo '--- Running kotlin compiler embeddable to collect reachability metadata ---'

  TMP_FILE=$(mktemp)
  mkdir -p resources/META-INF/native-image/org/jetbrains/kotlin/kotlin-compiler-embeddable
  echo 'fun main() { println("Hello world!") }' > "$TMP_FILE"

  $GRAAL_HOME/bin/java \
    --add-opens java.base/java.lang=ALL-UNNAMED \
    --add-opens java.base/java.io=ALL-UNNAMED \
    --add-opens java.base/java.nio=ALL-UNNAMED \
    --add-opens java.base/sun.nio.ch=ALL-UNNAMED \
    --add-opens java.desktop/javax.swing=ALL-UNNAMED \
    -cp "$CLASSPATH" \
    -Dkotlin.home=dist/kotlinc \
    -Djava.home="$JAVA_HOME" \
    -agentlib:native-image-agent=config-merge-dir=resources/META-INF/native-image/org/jetbrains/kotlin/kotlin-compiler-embeddable \
    org.jetbrains.kotlin.cli.jvm.K2JVMCompiler \
    -kotlin-home=dist/kotlinc \
    "$TMP_FILE"

  echo '--- Rebuilding kotlin compiler embeddable with reachability metadata ---'
  ./gradlew -q :kotlin-compiler-embeddable:embeddable
fi

echo '--- Prepare native image dist ---'
mkdir -p $KOTLINC_BINARY_DIST_DIR/
mkdir -p $KOTLINC_BINARY_DIST_DIR/bin
mkdir -p $KOTLINC_BINARY_DIST_DIR/lib
mkdir -p $KOTLINC_BINARY_DIST_DIR/resources

cp -r dist/kotlinc/lib/* $KOTLINC_BINARY_DIST_DIR/lib
cp -r compiler/cli/cli-common/build/resources/main/* $KOTLINC_BINARY_DIST_DIR/resources/

echo '--- Building native image of kotlin compiler embeddable ---'
$NATIVE_IMAGE_BIN \
  --add-opens java.base/java.lang=ALL-UNNAMED \
  --add-opens java.base/java.io=ALL-UNNAMED \
  --add-opens java.base/java.nio=ALL-UNNAMED \
  --add-opens java.base/sun.nio.ch=ALL-UNNAMED \
  --add-opens java.desktop/javax.swing=ALL-UNNAMED \
  -H:+AddAllCharsets \
  -H:+UnlockExperimentalVMOptions \
  -H:+AllowJRTFileSystem \
  -cp "$CLASSPATH" \
  -o $KOTLINC_BINARY_DIST_DIR/bin/$KOTLINC_BINARY_NAME \
  org.jetbrains.kotlin.cli.jvm.K2JVMCompiler

echo '--- Creating wrappers for the kotlinc native binary ---'
cp native-image/kotlinc-native.sh $KOTLINC_BINARY_DIST_DIR/bin/
chmod a+x $KOTLINC_BINARY_DIST_DIR/bin/kotlinc-native.sh

#!/usr/bin/env bash

fileCopy() {
  # Just to avoid arg3 being passed to cp, so that it can be interchanged with fileAsk

  if [[ ! -d "$(dirname "$2")" ]]; then
    mkdir -p "$(dirname "$2")"
  fi
  scp -qr "$1" "$2"
}
export -f fileCopy

ask() {
  # Inspired by http://djm.me/ask
  local QUESTION_ARG="$1"
  local DEFAULT_ARG="$2"
  declare -r QUESTION_ARG # final
  declare -r DEFAULT_ARG # final
  local PREFIX="[Input needed!]"

  while true; do
    local REPLY=""
    local PROMPT=""
    local DEFAULT=""

    # Setup defaults
    if [ "$DEFAULT_ARG" = "Y" ]; then
      PROMPT="Y/n"
      DEFAULT="Y"
    elif [ "$DEFAULT_ARG" = "N" ]; then
      PROMPT="y/N"
      DEFAULT="N"
    else
      PROMPT="y/n"
      DEFAULT=""
    fi

    # Ask the question
    read -p "$PREFIX $QUESTION_ARG? [$PROMPT]: " REPLY

    # Default?
    if [ -z "$REPLY" ]; then
      REPLY=$DEFAULT
    fi

    # Check if the reply is valid
    case "$REPLY" in
      Yes|yes|Y|y)
        return 0 ;;
      No|no|N|n)
        return 1 ;;
      *)
        PREFIX="[Invalid input]" ;;
    esac
  done
}
export -f ask

askString() {
  local QUESTION_ARG="$1"
  declare -r QUESTION_ARG # final
  local PREFIX="[Input needed!]"

  while true; do
    local REPLY=""
    local PROMPT=""
    local DEFAULT=""

    # Ask the question
    read -p "$PREFIX $QUESTION_ARG?: " REPLY

    # Check if the reply is valid
    if [ ! -z "$REPLY"  ]; then
      eval "$2=$REPLY"
      return 0
    else
      PREFIX="[Invalid input]"
    fi
  done
}
export -f askString

fileAsk() {
  local SOURCE_FILE_NAME_ARG="$1"
  local TARGET_FILE_NAME_ARG="$2"
  local FILE_DESC_ARG="$3"
  declare -r TARGET_FILE_NAME_ARG # final
  declare -r SOURCE_FILE_NAME_ARG # final
  declare -r FILE_DESC_ARG # final
  local PREFIX="[Input needed!]"
  local REPLY=""
  local DIFF_DIR=""

  local FILE_DESC=""
  if [[ -z "$FILE_DESC_ARG" ]]; then
    FILE_DESC="The"
  else
    FILE_DESC="The $FILE_DESC_ARG"
  fi
  declare -r FILE_DESC

  if [[ -f "$TARGET_FILE_NAME_ARG" ]]; then
    # Both files exist
    if [[ ! -z "$(rsync --dry-run -ci "$SOURCE_FILE_NAME_ARG" "$TARGET_FILE_NAME_ARG")" ]]; then
      # Files in conflict
      while true; do
        # Ask the question
        echo "$PREFIX $FILE_DESC file at ($TARGET_FILE_NAME_ARG) does not match the proposed update ($SOURCE_FILE_NAME_ARG)? [O/k/b/m/d]"
        echo "1) [o] Overwrite the existing file"
        echo "2) [b] Overwrite the existing file, but make a backup .old file"
        echo "3) [k] Keep the existing file"
        echo "4) [m] Keep the existing file, but make a proposed .new file (for manual intervention)"
        echo "5) [d] Show me a diff"
        read -p "Choice? [O/k/b/m/d]: " REPLY

        # Default?
        if [ -z "$REPLY" ]; then
          REPLY="O"
        fi

        # Check if the reply is valid
        case "$REPLY" in
          O|o|1)
            echo "Overwriting the existing file [$TARGET_FILE_NAME_ARG]"
            rsync -c "$SOURCE_FILE_NAME_ARG" "$TARGET_FILE_NAME_ARG"
            return 0 ;;
          B|b|2)
            echo "Overwriting the existing file, but make a backup .old file [$TARGET_FILE_NAME_ARG]"
            rsync -c --backup --suffix ".old" "$SOURCE_FILE_NAME_ARG" "$TARGET_FILE_NAME_ARG"
            return 0 ;;
          K|k|3)
            echo "Keeping the existing file [$TARGET_FILE_NAME_ARG]"
            return 0 ;;
          M|m|4)
            echo "Keeping the existing file, but make a proposed .new file (for manual intervention) [$TARGET_FILE_NAME_ARG]"
            rsync -c --backup --suffix ".old" "$SOURCE_FILE_NAME_ARG" "$TARGET_FILE_NAME_ARG"
            mv "$TARGET_FILE_NAME_ARG" "$TARGET_FILE_NAME_ARG.new"
            mv "$TARGET_FILE_NAME_ARG.old" "$TARGET_FILE_NAME_ARG"
            return 0 ;;
          D|d|5)
            DIFF_DIR="$(mktemp -d)"
            fileCopy "$TARGET_FILE_NAME_ARG" "$DIFF_DIR/old"
            fileCopy "$TARGET_FILE_NAME_ARG" "$DIFF_DIR/new"
            rsync -c "$SOURCE_FILE_NAME_ARG" "$DIFF_DIR/new"
            diff "$DIFF_DIR/new" "$DIFF_DIR/old" | less
            rm -r "$DIFF_DIR"
            PREFIX="[Diff complete]" ;;
          *)
            PREFIX="[Invalid input]" ;;
        esac
      done
    else
      echo "Not updating the file, as no changes were detected [$TARGET_FILE_NAME_ARG]"
    fi
  else
    echo "Writing first copy of the file [$TARGET_FILE_NAME_ARG]"
    fileCopy "$SOURCE_FILE_NAME_ARG" "$TARGET_FILE_NAME_ARG"
  fi
}
export -f fileAsk

dirAsk() {
  local SOURCE_DIR_NAME_ARG="$1"
  local TARGET_DIR_NAME_ARG="$2"
  local DIR_DESC_ARG="$3"
  declare -r TARGET_DIR_NAME_ARG # final
  declare -r SOURCE_DIR_NAME_ARG # final
  declare -r DIR_DESC_ARG # final
  local PREFIX="[Input needed!]"
  local REPLY=""

  local DIR_DESC=""
  if [[ -z "$DIR_DESC_ARG" ]]; then
    DIR_DESC="The"
  else
    DIR_DESC="The $DIR_DESC_ARG"
  fi
  declare -r DIR_DESC

  if [[ -d "$TARGET_DIR_NAME_ARG" ]]; then
    # Both files exist
    if [[ ! -z "$(rsync --dry-run -cirm --delete ${@:4} "$SOURCE_DIR_NAME_ARG" "$TARGET_DIR_NAME_ARG")" ]]; then
      # Files in conflict
      while true; do
        # Ask the question
        echo "$PREFIX $DIR_DESC dir at ($TARGET_DIR_NAME_ARG) does not match the proposed update ($SOURCE_DIR_NAME_ARG)? [Y/n/d]"
        echo "1) [y] Overwrite the existing dir"
        echo "2) [n] Keep the existing dir"
        echo "3) [d] Show me an rsync itemized dry-run"
        read -p "Choice? [Y/n/d]: " REPLY

        # Default?
        if [ -z "$REPLY" ]; then
          REPLY="Y"
        fi

        # Check if the reply is valid
        case "$REPLY" in
          Y|y|1)
            echo "Overwriting the existing dir [$TARGET_DIR_NAME_ARG]"
            rsync -crm --delete ${@:4} "$SOURCE_DIR_NAME_ARG" "$TARGET_DIR_NAME_ARG"
            return 0 ;;
          N|n|3)
            echo "Keeping the existing dir [$TARGET_DIR_NAME_ARG]"
            return 0 ;;
          D|d|5)
            rsync --dry-run -cirm --delete ${@:4} "$SOURCE_DIR_NAME_ARG" "$TARGET_DIR_NAME_ARG" | less
            PREFIX="[Dry run done!]" ;;
          *)
            PREFIX="[Invalid input]" ;;
        esac
      done
    else
      echo "Not updating the dir, as no changes were detected [$TARGET_DIR_NAME_ARG]"
    fi
  else
    echo "Writing first copy of the dir [$TARGET_DIR_NAME_ARG]"
    fileCopy "$SOURCE_DIR_NAME_ARG" "$TARGET_DIR_NAME_ARG"
  fi
}
export -f dirAsk

permissionFix() {
  chown -R "$1" "$2"
  chmod -R 664 "$2"
  find "$2" -type d -exec chmod +x {} \;
}
export -f permissionFix

installWar() {
  local WAR_ID="$1"
  local WAR_FILE="$2"
  local WAR_PATH="$3/"
  declare -r WAR_ID # final
  declare -r WAR_FILE # final
  declare -r WAR_PATH # final
  local WAR_TEMP_DIR="$(mktemp -d)"

  echo "[[ $WAR_ID | Preparing update ]]"
  mkdir -p "$WAR_TEMP_DIR/$WAR_PATH"
  unzip -q "$WAR_FILE" -d "$WAR_TEMP_DIR/$WAR_PATH"

  if [[ -f "$WAR_TEMP_DIR/$WAR_PATH/WEB-INF/classes/log4j.properties" ]]; then
    sed -i "s|^log4j\.rootLogger\=.*$|log4j.rootLogger=ERROR, logfile|g" "$WAR_TEMP_DIR/$WAR_PATH/WEB-INF/classes/log4j.properties"
  fi

  echo "[[ $WAR_ID | Performing update ]]"
  dirAsk "$WAR_TEMP_DIR/$WAR_PATH" "$WEBAPP_PATH/$WAR_PATH"

  echo "[[ $WAR_ID | Ensuring correct file permissions ]]"
  permissionFix "tomcat7:tomcat7" "$WEBAPP_PATH/$WAR_PATH"

  echo "[[ $WAR_ID | War Done ]]"
  rm -rf "$WAR_TEMP_DIR"
}
export -f installWar
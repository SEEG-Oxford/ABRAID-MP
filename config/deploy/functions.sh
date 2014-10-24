#!/usr/bin/env bash

fileCopy() {
  # Just to avoid arg3 being passed to cp, so that it can be interchanged with fileAsk
  if [[ ! -d "$(dirname "$2")" ]]; then
    mkdir -p "$(dirname "$2")"
  fi

  scp -q "$1" "$2"
}

ask() {
  # Inspired by http://djm.me/ask
  local QUESTION_ARG=$1
  local DEFAULT_ARG=$2
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

fileAsk() {
  local SOURCE_FILE_NAME_ARG=$1
  local TARGET_FILE_NAME_ARG=$2
  local FILE_DESC_ARG=$3
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
            fileCopy -q "$TARGET_FILE_NAME_ARG" "$DIFF_DIR/old"
            fileCopy -q "$TARGET_FILE_NAME_ARG" "$DIFF_DIR/new"
            rsync -c "$SOURCE_FILE_NAME_ARG" "$DIFF_DIR/new"
            diff "$DIFF_DIR/new" "$DIFF_DIR/old" | less
            rm -r "$DIFF_DIR"
            PREFIX="[Diff complete]" ;;
          *)
            PREFIX="[Invalid input]" ;;
        esac
      done
    else
      echo "Not updating the file, as no changes where detected [$TARGET_FILE_NAME_ARG]"
    fi
  else
    echo "Writing first copy of the file [$TARGET_FILE_NAME_ARG]"
    fileCopy -q "$SOURCE_FILE_NAME_ARG" "$TARGET_FILE_NAME_ARG"
  fi
}


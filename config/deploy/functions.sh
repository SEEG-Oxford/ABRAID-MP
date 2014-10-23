#!/usr/bin/env bash

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
  local FILE_DESC_ARG=$1
  local FILE_NAME_ARG=$2
  local NEW_FILE_NAME_ARG=$3
  declare -r FILE_DESC_ARG # final
  declare -r FILE_NAME_ARG # final
  declare -r NEW_FILE_NAME_ARG # final
  local PREFIX="[Input needed!]"
  local REPLY=""
  local DIFF_DIR=""

  if [[ -f "$FILE_NAME_ARG" ]]; then
    # Both files exist
    if [[ ! -z "$(rsync --dry-run -ci "$FILE_NAME_ARG" "$NEW_FILE_NAME_ARG")" ]]; then
      # Files in conflict
      while true; do
        # Ask the question
        echo "$PREFIX The $FILE_DESC_ARG file at ($FILE_NAME_ARG) does not match the proposed update ($NEW_FILE_NAME_ARG)? [O/k/b/m/d]"
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
            echo "Overwriting the existing $FILE_DESC_ARG file"
            rsync -c "$NEW_FILE_NAME_ARG" "$FILE_NAME_ARG"
            return 0 ;;
          B|b|2)
            echo "Overwriting the existing $FILE_DESC_ARG file, but make a backup .old file"
            rsync -c --backup --suffix ".old" "$NEW_FILE_NAME_ARG" "$FILE_NAME_ARG"
            return 0 ;;
          K|k|3)
            echo "Keeping the existing $FILE_DESC_ARG file"
            return 0 ;;
          M|m|4)
            echo "Keeping the existing $FILE_DESC_ARG file, but make a proposed .new file (for manual intervention)"
            rsync -c --backup --suffix ".old" "$NEW_FILE_NAME_ARG" "$FILE_NAME_ARG"
            mv "$FILE_NAME_ARG" "$FILE_NAME_ARG.new"
            mv "$FILE_NAME_ARG.old" "$FILE_NAME_ARG"
            return 0 ;;
          D|d|5)
            DIFF_DIR="$(mktemp -d)"
            scp -q "$FILE_NAME_ARG" "$DIFF_DIR/old"
            scp -q "$FILE_NAME_ARG" "$DIFF_DIR/new"
            rsync -c "$NEW_FILE_NAME_ARG" "$DIFF_DIR/new"
            diff "$DIFF_DIR/new" "$DIFF_DIR/old" | less
            rm -r "$DIFF_DIR"
            PREFIX="[Diff complete]" ;;
          *)
            PREFIX="[Invalid input]" ;;
        esac
      done
    else
      echo "Not updating the $FILE_DESC_ARG file, as no changes where detected"
    fi
  else
    echo "Writing first copy of the $FILE_DESC_ARG file"
    scp -q "$NEW_FILE_NAME_ARG" "$FILE_NAME_ARG"
  fi
}
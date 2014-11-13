#!/usr/bin/env bash
set -e

if [[ ! -d "$ABRAID_SUPPORT_PATH/conf/" ]]; then
  (
    echo "[[ CONFIG | Getting first clone of config repo ]]"
    mkdir -p "$ABRAID_SUPPORT_PATH/conf/"
    cd "$ABRAID_SUPPORT_PATH/conf/"
    git init
    git config core.sparsecheckout true
    for dir in "$@"
    do
      echo "$dir" >> .git/info/sparse-checkout
    done
  )
else
  (
    echo "[[ CONFIG | Updating config repo ]]"
    cd "$ABRAID_SUPPORT_PATH/conf/"
    git remote remove origin
  )
fi

(
  cd "$ABRAID_SUPPORT_PATH/conf/"
  git remote add origin "$REMOTE_USER@$CONFIG_PATH"
  echo "Performing git pull"
  if ! git pull -q --ff-only origin "$CONFIG_BRANCH"; then
    echo "[[ CONFIG | Conflicts or problems detected ]]"
    read -p "Dropping to subshell for manual resolution (use 'git pull origin $CONFIG_BRANCH' to start and 'exit' to finish). Press [enter] to continue ..."
    bash --rcfile <(echo "PS1='[Deployment] \u@\h:\w\$ '") -i
  fi
)

echo "[[ CONFIG | Ensuring correct file permissions ]]"
permissionFix "tomcat7:tomcat7" "$ABRAID_SUPPORT_PATH/conf"

echo "[[ CONFIG | Done ]]"

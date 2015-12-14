#  Licensed to the Rhiot under one or more
#  contributor license agreements.  See the NOTICE file distributed with
#  this work for additional information regarding copyright ownership.
#  The licenses this file to You under the Apache License, Version 2.0
#  (the "License"); you may not use this file except in compliance with
#  the License.  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#
# This script provides completion stuff for rhiot script
#

_rhiot()
{
    local cur=${COMP_WORDS[COMP_CWORD]}
    local prev=${COMP_WORDS[COMP_CWORD-1]}

    case "$prev" in
        rhiot)

            COMPREPLY=( $(compgen -W "--help raspbian-install scan deploy-gateway shell-start" -- $cur) )

        return 0
        ;;

        --help)

        return 0
        ;;
    esac
}

complete -F _rhiot rhiot

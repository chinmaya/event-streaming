curl https://api.github.com/events | \grep '"type":.*Event"' | sed -e 's/.* \"//' -e 's/\",//g' | sort -u

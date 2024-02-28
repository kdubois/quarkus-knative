URL=$(kn service describe quarked -o url)
hey -n 2000 -c 200 ${URL}

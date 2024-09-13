#!/bin/sh

# Create a configuration file with environment variables
cat <<EOF > /usr/share/nginx/html/env-config.js
window._env_ = {
  REACT_APP_API_URL: "$REACT_APP_API_URL"
};
EOF

# Start Nginx
exec "$@"
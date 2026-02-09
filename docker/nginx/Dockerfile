# ──────────────────────────────────────────────────────────
# SwiftApp ERP — Nginx Reverse Proxy
# ──────────────────────────────────────────────────────────
FROM nginx:1.27-alpine

LABEL maintainer="SwiftApp ERP <dev@swiftapp.ch>"
LABEL description="Nginx reverse proxy for SwiftApp ERP"

# Remove default config
RUN rm /etc/nginx/conf.d/default.conf

# Copy custom configuration
COPY nginx.conf /etc/nginx/nginx.conf
COPY conf.d/ /etc/nginx/conf.d/

# Create cache and log directories
RUN mkdir -p /var/cache/nginx /var/log/nginx \
    && chown -R nginx:nginx /var/cache/nginx /var/log/nginx

EXPOSE 80 443

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget -qO- http://localhost/health || exit 1


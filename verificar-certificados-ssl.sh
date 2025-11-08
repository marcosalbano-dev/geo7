#!/bin/bash

echo "=== VERIFICANDO CERTIFICADOS SSL ==="
echo ""

# Verificar se os certificados existem
if [ -f "/etc/ssl/geo7/fullchain.pem" ] && [ -f "/etc/ssl/geo7/privkey.pem" ]; then
    echo "✅ Certificados encontrados em /etc/ssl/geo7/"
    echo ""
    echo "Detalhes dos certificados:"
    ls -lh /etc/ssl/geo7/
    echo ""
    echo "Validade do certificado:"
    openssl x509 -in /etc/ssl/geo7/fullchain.pem -noout -dates 2>/dev/null || echo "⚠️ Não foi possível verificar a validade"
    echo ""
    echo "✅ Certificados estão prontos para uso!"
    echo ""
    echo "Próximos passos:"
    echo "1. Certifique-se de que o docker-compose.yml monta o volume:"
    echo "   - /etc/ssl/geo7:/etc/ssl/geo7:ro"
    echo ""
    echo "2. O nginx.conf já está configurado para usar:"
    echo "   - ssl_certificate /etc/ssl/geo7/fullchain.pem;"
    echo "   - ssl_certificate_key /etc/ssl/geo7/privkey.pem;"
else
    echo "❌ Certificados NÃO encontrados em /etc/ssl/geo7/"
    echo ""
    echo "Você precisa criar os certificados. Execute:"
    echo ""
    echo "sudo mkdir -p /etc/ssl/geo7"
    echo "sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 \\"
    echo "  -keyout /etc/ssl/geo7/privkey.pem \\"
    echo "  -out /etc/ssl/geo7/fullchain.pem"
    echo ""
    echo "Durante a criação, responda as perguntas (pode pressionar Enter para usar valores padrão)"
fi


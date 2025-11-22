import re

# Leer archivo
with open('src/main/resources/templates/horario.html', 'r', encoding='utf-8') as f:
    content = f.read()

# Template completo del select con todas las opciones
select_template = '''<select name="{name}" th:value="${{{thvalue}}}">
                  <option value="">-- Seleccionar --</option>
                  <option value="00:00">12:00 AM</option>
                  <option value="00:30">12:30 AM</option>
                  <option value="01:00">1:00 AM</option>
                  <option value="01:30">1:30 AM</option>
                  <option value="02:00">2:00 AM</option>
                  <option value="02:30">2:30 AM</option>
                  <option value="03:00">3:00 AM</option>
                  <option value="03:30">3:30 AM</option>
                  <option value="04:00">4:00 AM</option>
                  <option value="04:30">4:30 AM</option>
                  <option value="05:00">5:00 AM</option>
                  <option value="05:30">5:30 AM</option>
                  <option value="06:00">6:00 AM</option>
                  <option value="06:30">6:30 AM</option>
                  <option value="07:00">7:00 AM</option>
                  <option value="07:30">7:30 AM</option>
                  <option value="08:00">8:00 AM</option>
                  <option value="08:30">8:30 AM</option>
                  <option value="09:00">9:00 AM</option>
                  <option value="09:30">9:30 AM</option>
                  <option value="10:00">10:00 AM</option>
                  <option value="10:30">10:30 AM</option>
                  <option value="11:00">11:00 AM</option>
                  <option value="11:30">11:30 AM</option>
                  <option value="12:00">12:00 PM</option>
                  <option value="12:30">12:30 PM</option>
                  <option value="13:00">1:00 PM</option>
                  <option value="13:30">1:30 PM</option>
                  <option value="14:00">2:00 PM</option>
                  <option value="14:30">2:30 PM</option>
                  <option value="15:00">3:00 PM</option>
                  <option value="15:30">3:30 PM</option>
                  <option value="16:00">4:00 PM</option>
                  <option value="16:30">4:30 PM</option>
                  <option value="17:00">5:00 PM</option>
                  <option value="17:30">5:30 PM</option>
                  <option value="18:00">6:00 PM</option>
                  <option value="18:30">6:30 PM</option>
                  <option value="19:00">7:00 PM</option>
                  <option value="19:30">7:30 PM</option>
                  <option value="20:00">8:00 PM</option>
                  <option value="20:30">8:30 PM</option>
                  <option value="21:00">9:00 PM</option>
                  <option value="21:30">9:30 PM</option>
                  <option value="22:00">10:00 PM</option>
                  <option value="22:30">10:30 PM</option>
                  <option value="23:00">11:00 PM</option>
                  <option value="23:30">11:30 PM</option>
                </select>'''

# Reemplazar el select vacío de dia1_fin que el usuario creó
pattern_empty = r'<select\s+name="dia1_fin"\s+th:value="\$\{horariosPorDia\.get\(1\)\?\.horaFin\}"\s*>\s*</select>'
replacement = select_template.format(name="dia1_fin", thvalue="horariosPorDia.get(1)?.horaFin")
content = re.sub(pattern_empty, replacement, content, flags=re.DOTALL)

# Reemplazar todos los inputs type="time" restantes (días 2-7 inicio y fin)
for dia in range(2, 8):
    # Inicio
    pattern = r'<input\s+type="time"\s+name="dia' + str(dia) + r'_inicio"\s+th:value="\$\{horariosPorDia\.get\(' + str(dia) + r'\)\?\.horaInicio\}"\s*/>'
    replacement = select_template.format(name=f"dia{dia}_inicio", thvalue=f"horariosPorDia.get({dia})?.horaInicio")
    content = re.sub(pattern, replacement, content)
    
    # Fin
    pattern = r'<input\s+type="time"\s+name="dia' + str(dia) + r'_fin"\s+th:value="\$\{horariosPorDia\.get\(' + str(dia) + r'\)\?\.horaFin\}"\s*/>'
    replacement = select_template.format(name=f"dia{dia}_fin", thvalue=f"horariosPorDia.get({dia})?.horaFin")
    content = re.sub(pattern, replacement, content)

# Guardar archivo
with open('src/main/resources/templates/horario.html', 'w', encoding='utf-8') as f:
    f.write(content)

print('[OK] Reemplazados 13 inputs por selects con todas las opciones')

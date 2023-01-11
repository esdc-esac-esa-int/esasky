import yaml

def load_yaml() -> dict:
    with open("../main/webapp/api.yaml", "r") as stream:
        try:
            return yaml.safe_load(stream)
        except yaml.YAMLError as exc:
            print(exc)

def load_template():
    output = ""
    with open("api_template.html", "r") as stream:
        for line in stream:
            output += line
    return output.split('------PART2------')

def write_template_part(w_file, template_part):
    w_file.write(template_part)

def write_start_category(w_file, cat):
    output = '\n<!--***************\n***'
    output += cat
    output += '***\n*************** -->\n'
    output += '<hr />\n'
    output += '<button class="accordion">'
    output += cat
    output += '</button>\n'
    output += '<div class="panel" id="'
    output += cat.lower().replace(" ", "_")
    output += '">\n'
    w_file.write(output)

def write_end_category(w_file):
    output = '</div>\n\n'
    w_file.write(output)

def write_start_func(w_file, func_name):
    output = '<p><b>Event:&nbsp;</b></p>\n'
    output += '<p>'
    output += func_name
    output += '</p>\n'
    w_file.write(output)

def write_description(w_file, description):
    output = '<p><b>Description:&nbsp;</b></p>\n'
    output += '<p>'
    output += description
    output += '</p>\n'
    w_file.write(output)

def write_example(w_file, func_name, example):
    output = '<p><b>Example:&nbsp;</b></p>\n'
    output += '<p id="'
    output += func_name.lower().replace(' ', '_')
    output += '">'
    output += str(example)
    output += '</p>\n'
    output += '<p><button onclick="msg_func(\''
    output += func_name.lower().replace(' ', '_')
    output += '\')" type="button">Send</button></p>\n'
    w_file.write(output)

def write_param(w_file, param_name, param):
    output = '<p>'
    output += param_name
    output += ' '
    if 'type' in param.keys():
        output += param['type']
    if 'description' in param.keys() and param['description']:
        output += ' - '
        output += param['description']
        output += '</p>\n'
    w_file.write(output)

def write_parameters(w_file, params):
    output = '<p><b>Parameters:&nbsp;</b></p>\n'
    w_file.write(output)
    for param_name in params.keys():
        write_param(w_file, param_name, params[param_name])

def write_events(w_file, events):
    output = '<p><b>Events:&nbsp;</b></p>\n'
    w_file.write(output)
    for event_name in events.keys():
        write_event(w_file, event_name, events[event_name])
        w_file.write('<p>&nbsp;</p>\n')

def write_event(w_file, event_name, event):
    output = '<p><b>'
    output += event_name
    output += '</b></p>\n'
    w_file.write(output)
    for param_name in event.keys():
        write_param(w_file, param_name, event[param_name])

def write_returns(w_file, params):
    output = '<p><b>Returns:&nbsp;</b></p>\n'
    w_file.write(output)
    for param_name in params.keys():
        write_param(w_file, param_name, params[param_name])

def write_function(w_file, func_name, func):
    write_start_func(w_file, func_name)
    if 'description' in func.keys():
        write_description(w_file, func['description'])
    if 'parameters' in func.keys():
        write_parameters(w_file,  func['parameters'])
    if 'returns' in func.keys():
        write_parameters(w_file,  func['returns'])
    if 'events' in func.keys():
        write_events(w_file,  func['events'])
    if 'example' in func.keys():
        write_example(w_file, func_name, func['example'])
    w_file.write('<p>&nbsp;</p>\n\n')

def main():
    with open("api.html", "w") as w_file:
        categories = load_yaml()
        template = load_template()
        write_template_part(w_file, template[0])
        for cat in categories.keys():
            write_start_category(w_file, cat)
            for func_name in categories[cat].keys():
                print(func_name)
                write_function(w_file, func_name, categories[cat][func_name])
            write_end_category(w_file)
        w_file.write('</div>')
        write_template_part(w_file, template[1])
if __name__ == "__main__":
    main()

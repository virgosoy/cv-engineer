export async function ${method.name}(<#list method.parameters as parameter>${parameter.name} : ${parameter.type?keep_after_last(".")}<#sep>,</#list>): Promise<${method.returnType}> {
    return api.post(`${class.requestMappingValue}${method.requestMappingValue?ensure_starts_with("/")}`, <#list method.parameters as parameter>${parameter.name}<#sep>,</#list>)
}
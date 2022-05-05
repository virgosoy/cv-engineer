export async function ${method.name}(<#list method.parameters as parameter>${parameter.name} : ${parameter.type}<#sep>,</#list>): Promise<${method.returnType}> {
    return api.${method.requestMethod}(`${class.requestMappingValue!}${method.requestMappingValue?ensure_starts_with("/")}`<#list method.parameters as parameter>, ${parameter.name}</#list>)
}
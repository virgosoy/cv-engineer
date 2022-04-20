export type ${class} = {
<#list fields as field>
    ${field.name} : ${field.type}
</#list>
}
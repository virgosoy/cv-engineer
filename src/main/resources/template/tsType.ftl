export type ${class} = {
<#list fields as field>
    ${field.name} : ${field.tsType}
</#list>
}
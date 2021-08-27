#	* Rego Cheat Sheet: https://www.openpolicyagent.org/docs/latest/policy-cheatsheet/
#	* Rego Built-in Functions: https://www.openpolicyagent.org/docs/latest/policy-reference/

package ciam.authz.external

default allow = false

allow {
    check_result.Result = true
}

check_result := ketoCheck(input.subject, input.relation, input.namespace, input.object)
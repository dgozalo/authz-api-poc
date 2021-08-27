package ciam.authz.internal

default allow = false

checkInputSubjectInArray(identityProvidersInput) = {a | a := identityProvidersInput[_]} - {b | b := input.subject}

allow {
	inputSubjectInRelation
}


inputSubjectInRelation() {
   arr := checkInputSubjectInArray(data.namespaces[input.namespace][input.object][input.relation])
   count(arr) != count(data.namespaces[input.namespace][input.object][input.relation])
}

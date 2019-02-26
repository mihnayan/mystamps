*** Settings ***
Documentation    Verify participant creation validation scenarios
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Resource         ../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       participant  validation

*** Test Cases ***
Create participant with blank required fields
	Remove Element Attribute  name  required
	Submit Form               id=add-participant-form
	Element Text Should Be    id=name.errors  Value must not be empty

Create participant with too short name
	Input Text              id=name  xx
	Submit Form             id=add-participant-form
	Element Text Should Be  id=name.errors  Value is less than allowable minimum of 3 characters

Create participant with too long name and url
	${letter}=              Set Variable  j
	Input Text              id=name  ${letter * 51}
	Input Text              id=url   http://${letter * 255}
	Submit Form             id=add-participant-form
	Element Text Should Be  id=name.errors  Value is greater than allowable maximum of 50 characters
	Element Text Should Be  id=url.errors   Value is greater than allowable maximum of 255 characters

Create participant with invalid url
	Input Text              id=url  invalid-url
	Submit Form             id=add-participant-form
	Element Text Should Be  id=url.errors  Value must be a valid URL

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/participant/add

After Test Suite
	Log Out
	Close Browser

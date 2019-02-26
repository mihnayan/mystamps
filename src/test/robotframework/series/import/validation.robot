*** Settings ***
Documentation    Verify import series validation scenarios
Library          SeleniumLibrary
Resource         ../../auth.steps.robot
Resource         ../../selenium.utils.robot
Suite Setup      Before Test Suite
Suite Teardown   After Test Suite
Force Tags       series  import-series  validation

*** Test Cases ***
Submit request with blank required field
	Submit Form                 id=import-series-form
	Wait Until Element Text Is  url.errors  Value must not be empty

Submit request with too long url
	${letter}=                  Set Variable  j
	Input Text                  id=url  http://${letter * 767}
	Submit Form                 id=import-series-form
	Wait Until Element Text Is  url.errors  Value is greater than allowable maximum of 767 characters

Submit request with invalid url
	Input Text                  id=url  invalid-url
	Submit Form                 id=import-series-form
	Wait Until Element Text Is  url.errors  Value must be a valid URL

*** Keywords ***
Before Test Suite
	Open Browser                        ${SITE_URL}/account/auth  ${BROWSER}
	Register Keyword To Run On Failure  Log Source
	Log In As                           login=admin  password=test
	Go To                               ${SITE_URL}/series/import/request

After Test Suite
	Log Out
	Close Browser

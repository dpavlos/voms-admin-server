<%--

    Copyright (c) Members of the EGEE Collaboration. 2006-2009.
    See http://www.eu-egee.org/partners/ for details on the copyright holders.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    Authors:
    	Andrea Ceccanti (INFN)

--%>
<%@include file="/WEB-INF/p/shared/taglibs.jsp"%>

<h1>Confirmation required.</h1>

<p>An email has been sent to you with instructions on how to proceed
with the registration for the ${voName} VO.</p>

<p>
Please follow the instructions before the following date:
</p>
<ul style="width: 100%; margin: 1em 0 1em 0;">
  <li style="text-align: center;"> 
    <span style="font-weight: bold; font-size: 20px;">
      <s:text name="format.datetime">
        <s:param value="model.expirationDate"/>
      </s:text>
    </span>
</li>
</ul>
or your request will be discarded by voms-admin.
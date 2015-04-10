set define off
set verify off
set feedback off
WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK
begin wwv_flow.g_import_in_progress := true; end;
/
 
 
--application/set_environment
prompt  APPLICATION 101 - Alpha Office Development
--
-- Application Export:
--   Application:     101
--   Name:            Alpha Office Development
--   Date and Time:   13:13 Thursday April 9, 2015
--   Exported By:     ADMIN
--   Flashback:       0
--   Export Type:     Application Export
--   Version:         4.2.5.00.08
--   Instance ID:     69430500808074
--
-- Import:
--   Using Application Builder
--   or
--   Using SQL*Plus as the Oracle user APEX_040200 or as the owner (parsing schema) of the application
 
-- Application Statistics:
--   Pages:                      5
--     Items:                    5
--     Processes:                4
--     Regions:                  8
--     Buttons:                  2
--   Shared Components:
--     Logic:
--     Navigation:
--       Tab Sets:               1
--         Tabs:                 1
--       Breadcrumbs:            1
--       NavBar Entries:         1
--     Security:
--       Authentication:         1
--     User Interface:
--       Themes:                 1
--       Templates:
--         Page:                 2
--         Region:              16
--         Label:                3
--         List:                 4
--         Popup LOV:            1
--         Calendar:             1
--         Breadcrumb:           1
--         Button:               3
--         Report:               1
--       LOVs:                   1
--     Globalization:
--     Reports:
 
 
--       AAAA       PPPPP   EEEEEE  XX      XX
--      AA  AA      PP  PP  EE       XX    XX
--     AA    AA     PP  PP  EE        XX  XX
--    AAAAAAAAAA    PPPPP   EEEE       XXXX
--   AA        AA   PP      EE        XX  XX
--  AA          AA  PP      EE       XX    XX
--  AA          AA  PP      EEEEEE  XX      XX
prompt  Set Credentials...
 
begin
 
  -- Assumes you are running the script connected to SQL*Plus as the Oracle user APEX_040200 or as the owner (parsing schema) of the application.
  wwv_flow_api.set_security_group_id(p_security_group_id=>nvl(wwv_flow_application_install.get_workspace_id,2222523363265404));
 
end;
/

begin wwv_flow.g_import_in_progress := true; end;
/
begin 

select value into wwv_flow_api.g_nls_numeric_chars from nls_session_parameters where parameter='NLS_NUMERIC_CHARACTERS';

end;

/
begin execute immediate 'alter session set nls_numeric_characters=''.,''';

end;

/
begin wwv_flow.g_browser_language := 'en'; end;
/
prompt  Check Compatibility...
 
begin
 
-- This date identifies the minimum version required to import this file.
wwv_flow_api.set_version(p_version_yyyy_mm_dd=>'2012.01.01');
 
end;
/

prompt  Set Application ID...
 
begin
 
   -- SET APPLICATION ID
   wwv_flow.g_flow_id := nvl(wwv_flow_application_install.get_application_id,101);
   wwv_flow_api.g_id_offset := nvl(wwv_flow_application_install.get_offset,0);
null;
 
end;
/

--application/delete_application
 
begin
 
   -- Remove Application
wwv_flow_api.remove_flow(nvl(wwv_flow_application_install.get_application_id,101));
 
end;
/

 
begin
 
wwv_flow_audit.remove_audit_trail(nvl(wwv_flow_application_install.get_application_id,101));
null;
 
end;
/

prompt  ...ui types
--
 
begin
 
null;
 
end;
/

--application/create_application
 
begin
 
wwv_flow_api.create_flow(
  p_id    => nvl(wwv_flow_application_install.get_application_id,101),
  p_display_id=> nvl(wwv_flow_application_install.get_application_id,101),
  p_owner => nvl(wwv_flow_application_install.get_schema,'ALPHA'),
  p_name  => nvl(wwv_flow_application_install.get_application_name,'Alpha Office Development'),
  p_alias => nvl(wwv_flow_application_install.get_application_alias,'F_101'),
  p_page_view_logging => 'YES',
  p_page_protection_enabled_y_n=> 'Y',
  p_checksum_salt=> 'B4AD2F6FFDC49D09A56CB1FDD4BA9238401E11E80D8275AD90C12AB1EED7B3B8',
  p_max_session_length_sec=> null,
  p_compatibility_mode=> '4.2',
  p_html_escaping_mode=> 'E',
  p_flow_language=> 'en',
  p_flow_language_derived_from=> 'FLOW_PRIMARY_LANGUAGE',
  p_allow_feedback_yn=> 'N',
  p_flow_image_prefix => nvl(wwv_flow_application_install.get_image_prefix,''),
  p_publish_yn=> 'N',
  p_documentation_banner=> '',
  p_authentication=> 'PLUGIN',
  p_authentication_id=> 2227607043528968 + wwv_flow_api.g_id_offset,
  p_logout_url=> '',
  p_application_tab_set=> 0,
  p_logo_image => 'TEXT:Alpha Office Development',
  p_public_url_prefix => '',
  p_public_user=> 'APEX_PUBLIC_USER',
  p_dbauth_url_prefix => '',
  p_proxy_server=> nvl(wwv_flow_application_install.get_proxy,''),
  p_cust_authentication_process=> '',
  p_cust_authentication_page=> '',
  p_flow_version=> 'release 1.0',
  p_flow_status=> 'AVAILABLE_W_EDIT_LINK',
  p_flow_unavailable_text=> 'This application is currently unavailable at this time.',
  p_build_status=> 'RUN_AND_BUILD',
  p_exact_substitutions_only=> 'Y',
  p_browser_cache=>'N',
  p_browser_frame=>'D',
  p_deep_linking=>'Y',
  p_vpd=> '',
  p_vpd_teardown_code=> '',
  p_authorize_public_pages_yn=>'N',
  p_csv_encoding=> 'Y',
  p_include_legacy_javascript=> 'N',
  p_default_error_display_loc=> 'INLINE_WITH_FIELD_AND_NOTIFICATION',
  p_last_updated_by => 'ADMIN',
  p_last_upd_yyyymmddhh24miss=> '20150409131238',
  p_ui_type_name => null,
  p_required_roles=> wwv_flow_utilities.string_to_table2(''));
 
 
end;
/

----------------
--package app map
--
prompt  ...user interfaces
--
 
begin
 
--application/user interface/jquery_mobile_smartphone
wwv_flow_api.create_user_interface (
  p_id => 2227404414528941 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_ui_type_name => 'JQM_SMARTPHONE'
 ,p_display_name => 'jQuery Mobile Smartphone'
 ,p_display_seq => 10
 ,p_use_auto_detect => false
 ,p_is_default => true
 ,p_theme_id => 50
 ,p_home_url => 'f?p=&APP_ID.:1:&SESSION.'
 ,p_login_url => 'f?p=&APP_ID.:LOGIN_JQM_SMARTPHONE:&SESSION.'
 ,p_global_page_id => 0
  );
null;
 
end;
/

prompt  ...plug-in settings
--
 
begin
 
--application/plug-in setting/item_type_native_yes_no
wwv_flow_api.create_plugin_setting (
  p_id => 2224000733528776 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_plugin_type => 'ITEM TYPE'
 ,p_plugin => 'NATIVE_YES_NO'
 ,p_attribute_01 => 'Y'
 ,p_attribute_03 => 'N'
  );
null;
 
end;
/

prompt  ...authorization schemes
--
 
begin
 
null;
 
end;
/

--application/shared_components/navigation/navigation_bar
prompt  ...navigation bar entries
--
 
begin
 
wwv_flow_api.create_icon_bar_item(
  p_id => 2227523693528961 + wwv_flow_api.g_id_offset,
  p_flow_id => wwv_flow.g_flow_id,
  p_icon_sequence=> 200,
  p_icon_image => '',
  p_icon_subtext=> 'Logout',
  p_icon_target=> '&LOGOUT_URL.',
  p_icon_image_alt=> 'Logout',
  p_icon_height=> 32,
  p_icon_width=> 32,
  p_icon_height2=> 24,
  p_icon_width2=> 24,
  p_nav_entry_is_feedback_yn => 'N',
  p_icon_bar_disp_cond=> '',
  p_icon_bar_disp_cond_type=> '',
  p_begins_on_new_line=> '',
  p_cell_colspan      => 1,
  p_onclick=> '',
  p_icon_bar_comment=> '');
 
 
end;
/

prompt  ...application processes
--
prompt  ...application items
--
prompt  ...application level computations
--
 
begin
 
null;
 
end;
/

prompt  ...Application Tabs
--
 
begin
 
--application/shared_components/navigation/tabs/standard/t_home
wwv_flow_api.create_tab (
  p_id=> 2229629276529071 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_tab_set=> 'TS1',
  p_tab_sequence=> 10,
  p_tab_name=> 'T_HOME',
  p_tab_text => 'Home',
  p_tab_step => 1,
  p_tab_also_current_for_pages => '',
  p_tab_parent_tabset=>'',
  p_tab_comment  => '');
 
 
end;
/

prompt  ...Application Parent Tabs
--
 
begin
 
null;
 
end;
/

prompt  ...Shared Lists of values
--
--application/shared_components/user_interface/lov/p2_report_row_per_page
 
begin
 
wwv_flow_api.create_list_of_values (
  p_id       => 2231122072564906 + wwv_flow_api.g_id_offset,
  p_flow_id  => wwv_flow.g_flow_id,
  p_lov_name => 'P2_Report Row Per Page',
  p_lov_query=> '.'||to_char(2231122072564906 + wwv_flow_api.g_id_offset)||'.');
 
null;
 
end;
/

 
begin
 
wwv_flow_api.create_static_lov_data (
  p_id=>2231408036564919 + wwv_flow_api.g_id_offset,
  p_lov_id=>2231122072564906 + wwv_flow_api.g_id_offset,
  p_lov_disp_sequence=>10,
  p_lov_disp_value=>'10',
  p_lov_return_value=>'10',
  p_lov_data_comment=> '');
 
null;
 
end;
/

 
begin
 
wwv_flow_api.create_static_lov_data (
  p_id=>2231725254564923 + wwv_flow_api.g_id_offset,
  p_lov_id=>2231122072564906 + wwv_flow_api.g_id_offset,
  p_lov_disp_sequence=>20,
  p_lov_disp_value=>'15',
  p_lov_return_value=>'15',
  p_lov_data_comment=> '');
 
null;
 
end;
/

 
begin
 
wwv_flow_api.create_static_lov_data (
  p_id=>2232027295564924 + wwv_flow_api.g_id_offset,
  p_lov_id=>2231122072564906 + wwv_flow_api.g_id_offset,
  p_lov_disp_sequence=>30,
  p_lov_disp_value=>'20',
  p_lov_return_value=>'20',
  p_lov_data_comment=> '');
 
null;
 
end;
/

 
begin
 
wwv_flow_api.create_static_lov_data (
  p_id=>2232304932564924 + wwv_flow_api.g_id_offset,
  p_lov_id=>2231122072564906 + wwv_flow_api.g_id_offset,
  p_lov_disp_sequence=>40,
  p_lov_disp_value=>'30',
  p_lov_return_value=>'30',
  p_lov_data_comment=> '');
 
null;
 
end;
/

 
begin
 
wwv_flow_api.create_static_lov_data (
  p_id=>2232604477564925 + wwv_flow_api.g_id_offset,
  p_lov_id=>2231122072564906 + wwv_flow_api.g_id_offset,
  p_lov_disp_sequence=>50,
  p_lov_disp_value=>'50',
  p_lov_return_value=>'50',
  p_lov_data_comment=> '');
 
null;
 
end;
/

 
begin
 
wwv_flow_api.create_static_lov_data (
  p_id=>2232921393564925 + wwv_flow_api.g_id_offset,
  p_lov_id=>2231122072564906 + wwv_flow_api.g_id_offset,
  p_lov_disp_sequence=>60,
  p_lov_disp_value=>'100',
  p_lov_return_value=>'100',
  p_lov_data_comment=> '');
 
null;
 
end;
/

 
begin
 
wwv_flow_api.create_static_lov_data (
  p_id=>2233227206564926 + wwv_flow_api.g_id_offset,
  p_lov_id=>2231122072564906 + wwv_flow_api.g_id_offset,
  p_lov_disp_sequence=>70,
  p_lov_disp_value=>'200',
  p_lov_return_value=>'200',
  p_lov_data_comment=> '');
 
null;
 
end;
/

 
begin
 
wwv_flow_api.create_static_lov_data (
  p_id=>2233512833564926 + wwv_flow_api.g_id_offset,
  p_lov_id=>2231122072564906 + wwv_flow_api.g_id_offset,
  p_lov_disp_sequence=>80,
  p_lov_disp_value=>'500',
  p_lov_return_value=>'500',
  p_lov_data_comment=> '');
 
null;
 
end;
/

 
begin
 
wwv_flow_api.create_static_lov_data (
  p_id=>2233803225564926 + wwv_flow_api.g_id_offset,
  p_lov_id=>2231122072564906 + wwv_flow_api.g_id_offset,
  p_lov_disp_sequence=>90,
  p_lov_disp_value=>'1000',
  p_lov_return_value=>'1000',
  p_lov_data_comment=> '');
 
null;
 
end;
/

 
begin
 
wwv_flow_api.create_static_lov_data (
  p_id=>2234120715564926 + wwv_flow_api.g_id_offset,
  p_lov_id=>2231122072564906 + wwv_flow_api.g_id_offset,
  p_lov_disp_sequence=>100,
  p_lov_disp_value=>'5000',
  p_lov_return_value=>'5000',
  p_lov_data_comment=> '');
 
null;
 
end;
/

prompt  ...Application Trees
--
--application/pages/page_groups
prompt  ...page groups
--
 
begin
 
null;
 
end;
/

--application/comments
prompt  ...comments: requires application express 2.2 or higher
--
 
--application/pages/page_00000
prompt  ...PAGE 0: Global Page - jQuery Mobile Smartphone
--
 
begin
 
wwv_flow_api.create_page (
  p_flow_id => wwv_flow.g_flow_id
 ,p_id => 0
 ,p_user_interface_id => 2227404414528941 + wwv_flow_api.g_id_offset
 ,p_name => 'Global Page - jQuery Mobile Smartphone'
 ,p_step_title => 'Global Page - jQuery Mobile Smartphone'
 ,p_step_sub_title_type => 'TEXT_WITH_SUBSTITUTIONS'
 ,p_first_item => 'NO_FIRST_ITEM'
 ,p_include_apex_css_js_yn => 'Y'
 ,p_autocomplete_on_off => 'ON'
 ,p_page_is_public_y_n => 'N'
 ,p_protection_level => 'D'
 ,p_cache_page_yn => 'N'
 ,p_last_upd_yyyymmddhh24miss => '20150409104407'
  );
null;
 
end;
/

declare
  s varchar2(32767) := null;
  l_clob clob;
  l_length number := 1;
begin
s := null;
wwv_flow_api.create_page_plug (
  p_id=> 2229018421529055 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_page_id=> 0,
  p_plug_name=> 'Header',
  p_region_name=>'',
  p_escape_on_http_output=>'N',
  p_plug_template=> 2225409203528836+ wwv_flow_api.g_id_offset,
  p_plug_display_sequence=> 10,
  p_plug_new_grid         => false,
  p_plug_new_grid_row     => true,
  p_plug_new_grid_column  => true,
  p_plug_display_column=> null,
  p_plug_display_point=> 'REGION_POSITION_01',
  p_plug_item_display_point=> 'ABOVE',
  p_plug_source=> s,
  p_plug_source_type=> 'STATIC_TEXT',
  p_translate_title=> 'Y',
  p_plug_query_row_template=> 1,
  p_plug_query_headings_type=> 'COLON_DELMITED_LIST',
  p_plug_query_row_count_max => 500,
  p_plug_display_condition_type => 'CURRENT_PAGE_NOT_IN_CONDITION',
  p_plug_display_when_condition => '101',
  p_plug_customized=>'0',
  p_plug_caching=> 'NOT_CACHED',
  p_plug_comment=> 'Header');
end;
/
declare
  s varchar2(32767) := null;
  l_clob clob;
  l_length number := 1;
begin
s := null;
wwv_flow_api.create_page_plug (
  p_id=> 2229330585529066 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_page_id=> 0,
  p_plug_name=> 'Footer',
  p_region_name=>'',
  p_escape_on_http_output=>'N',
  p_plug_template=> 2225211651528835+ wwv_flow_api.g_id_offset,
  p_plug_display_sequence=> 100,
  p_plug_new_grid         => false,
  p_plug_new_grid_row     => true,
  p_plug_new_grid_column  => true,
  p_plug_display_column=> null,
  p_plug_display_point=> 'REGION_POSITION_08',
  p_plug_item_display_point=> 'ABOVE',
  p_plug_source=> s,
  p_plug_source_type=> 'STATIC_TEXT',
  p_translate_title=> 'Y',
  p_plug_query_row_template=> 1,
  p_plug_query_headings_type=> 'COLON_DELMITED_LIST',
  p_plug_query_row_count_max => 500,
  p_plug_display_condition_type => 'CURRENT_PAGE_NOT_IN_CONDITION',
  p_plug_display_when_condition => '101',
  p_plug_customized=>'0',
  p_plug_caching=> 'NOT_CACHED',
  p_plug_comment=> 'Footer');
end;
/
 
begin
 
wwv_flow_api.create_page_button(
  p_id             => 2229212859529066 + wwv_flow_api.g_id_offset,
  p_flow_id        => wwv_flow.g_flow_id,
  p_flow_step_id   => 0,
  p_button_sequence=> 20,
  p_button_plug_id => 2229018421529055+wwv_flow_api.g_id_offset,
  p_button_name    => 'LOGOUT',
  p_button_action  => 'REDIRECT_URL',
  p_button_image   => 'template:'||to_char(2226817914528893+wwv_flow_api.g_id_offset),
  p_button_is_hot=>'N',
  p_button_image_alt=> 'Logout',
  p_button_position=> 'REGION_TEMPLATE_NEXT',
  p_button_alignment=> 'RIGHT',
  p_button_redirect_url=> 'javascript:location.href="&LOGOUT_URL.";',
  p_required_patch => null + wwv_flow_api.g_id_offset);
 
wwv_flow_api.create_page_button(
  p_id             => 2229121820529056 + wwv_flow_api.g_id_offset,
  p_flow_id        => wwv_flow.g_flow_id,
  p_flow_step_id   => 0,
  p_button_sequence=> 10,
  p_button_plug_id => 2229018421529055+wwv_flow_api.g_id_offset,
  p_button_name    => 'HOME',
  p_button_action  => 'REDIRECT_PAGE',
  p_button_image   => 'template:'||to_char(2226817914528893+wwv_flow_api.g_id_offset),
  p_button_is_hot=>'N',
  p_button_image_alt=> 'Home',
  p_button_position=> 'REGION_TEMPLATE_PREVIOUS',
  p_button_alignment=> 'RIGHT',
  p_button_redirect_url=> 'f?p=&APP_ID.:1:&APP_SESSION.::&DEBUG.:::',
  p_button_cattributes=>'data-icon="home" data-iconpos="notext" data-direction="reverse"',
  p_required_patch => null + wwv_flow_api.g_id_offset);
 
 
end;
/

 
begin
 
null;
 
end;
/

 
begin
 
---------------------------------------
-- ...updatable report columns for page 0
--
 
begin
 
null;
end;
null;
 
end;
/

 
--application/pages/page_00001
prompt  ...PAGE 1: Home
--
 
begin
 
wwv_flow_api.create_page (
  p_flow_id => wwv_flow.g_flow_id
 ,p_id => 1
 ,p_user_interface_id => 2227404414528941 + wwv_flow_api.g_id_offset
 ,p_tab_set => 'TS1'
 ,p_name => 'Home'
 ,p_step_title => 'Home'
 ,p_allow_duplicate_submissions => 'Y'
 ,p_step_sub_title => 'Home'
 ,p_step_sub_title_type => 'TEXT_WITH_SUBSTITUTIONS'
 ,p_include_apex_css_js_yn => 'Y'
 ,p_autocomplete_on_off => 'ON'
 ,p_page_is_public_y_n => 'Y'
 ,p_protection_level => 'N'
 ,p_cache_page_yn => 'N'
 ,p_cache_timeout_seconds => 21600
 ,p_cache_by_user_yn => 'N'
 ,p_help_text => 
'No help is available for this page.'
 ,p_last_updated_by => 'ADMIN'
 ,p_last_upd_yyyymmddhh24miss => '20150409130404'
  );
null;
 
end;
/

declare
  s varchar2(32767) := null;
  l_clob clob;
  l_length number := 1;
begin
s:=s||'Welcome to the Alpha Office application';

wwv_flow_api.create_page_plug (
  p_id=> 2230409107538349 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_page_id=> 1,
  p_plug_name=> 'Welcome',
  p_region_name=>'',
  p_escape_on_http_output=>'Y',
  p_plug_template=> 2225601475528836+ wwv_flow_api.g_id_offset,
  p_plug_display_sequence=> 10,
  p_plug_new_grid         => false,
  p_plug_new_grid_row     => true,
  p_plug_new_grid_column  => true,
  p_plug_display_column=> null,
  p_plug_display_point=> 'BODY_3',
  p_plug_item_display_point=> 'ABOVE',
  p_plug_source=> s,
  p_plug_source_type=> 'STATIC_TEXT',
  p_plug_query_row_template=> 1,
  p_plug_query_headings_type=> 'QUERY_COLUMNS',
  p_plug_query_num_rows => 15,
  p_plug_query_num_rows_type => 'NEXT_PREVIOUS_LINKS',
  p_plug_query_row_count_max => 500,
  p_plug_query_show_nulls_as => ' - ',
  p_plug_display_condition_type => '',
  p_pagination_display_position=>'BOTTOM_RIGHT',
  p_plug_caching=> 'NOT_CACHED',
  p_plug_comment=> '');
end;
/
declare
  s varchar2(32767) := null;
  l_clob clob;
  l_length number := 1;
begin
s := null;
wwv_flow_api.create_page_plug (
  p_id=> 2242525261309382 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_page_id=> 1,
  p_plug_name=> 'Product Categories',
  p_region_name=>'',
  p_escape_on_http_output=>'N',
  p_plug_template=> 2225601475528836+ wwv_flow_api.g_id_offset,
  p_plug_display_sequence=> 20,
  p_plug_new_grid         => false,
  p_plug_new_grid_row     => true,
  p_plug_new_grid_column  => true,
  p_plug_display_column=> null,
  p_plug_display_point=> 'BODY_3',
  p_plug_item_display_point=> 'ABOVE',
  p_plug_source=> s,
  p_plug_source_type=> 'FLASH_CHART5',
  p_translate_title=> 'Y',
  p_plug_query_row_template=> 1,
  p_plug_query_headings_type=> 'COLON_DELMITED_LIST',
  p_plug_query_row_count_max => 500,
  p_plug_display_condition_type => '',
  p_plug_customized=>'0',
  p_plug_caching=> 'NOT_CACHED',
  p_plug_comment=> '');
end;
/
declare
 a1 varchar2(32767) := null;
begin
a1 := null;
wwv_flow_api.create_flash_chart5(
  p_id                     => 2242716028309399+wwv_flow_api.g_id_offset,
  p_flow_id                => wwv_flow.g_flow_id,
  p_page_id                => 1,
  p_region_id              => 2242525261309382+wwv_flow_api.g_id_offset,
  p_default_chart_type     =>'2DPie',
  p_chart_title            =>'Product Categories',
  p_chart_rendering        =>'SVG_ONLY',
  p_chart_name             =>'chart_2242716028309399',
  p_chart_width            =>null,
  p_chart_height           =>null,
  p_chart_animation        =>'Appear',
  p_display_attr           =>':H:N:V:::N::V:Y:None:::N:::Default:::S',
  p_dial_tick_attr         =>':::::::::::',
  p_gantt_attr             =>'Y:Rhomb:Rhomb:Full:Rhomb:Rhomb:Full:Rhomb:Rhomb:Full:30:15:5:Y:I:N:S:E::',
  p_pie_attr               =>'Inside:::',
  p_map_attr               =>'Orthographic:RegionBounds:REGION_NAME',
  p_map_source             =>'%',
  p_margins                =>':::',
  p_omit_label_interval    => null,
  p_bgtype                 =>'Gradient',
  p_bgcolor1               =>'#eeeeee',
  p_bgcolor2               =>'#eeeeee',
  p_gradient_rotation      =>0,
  p_grid_bgtype            =>'',
  p_grid_bgcolor1          =>'',
  p_grid_bgcolor2          =>'',
  p_grid_gradient_rotation =>null,
  p_color_scheme           =>'6',
  p_custom_colors          =>'',
  p_map_undef_color_scheme =>'',
  p_map_undef_custom_colors =>'',
  p_x_axis_title           =>'',
  p_x_axis_min             =>null,
  p_x_axis_max             =>null,
  p_x_axis_decimal_place   =>null,
  p_x_axis_prefix          =>'',
  p_x_axis_postfix         =>'',
  p_x_axis_label_rotation  =>'',
  p_x_axis_label_font      =>'Tahoma:10:#000000',
  p_x_axis_major_interval  =>null,
  p_x_axis_minor_interval  =>null,
  p_y_axis_title           =>'',
  p_y_axis_min             =>null,
  p_y_axis_max             =>null,
  p_y_axis_decimal_place   =>null,
  p_y_axis_prefix          =>'',
  p_y_axis_postfix         =>'',
  p_y_axis_label_rotation  =>'',
  p_y_axis_label_font      =>'Tahoma:10:#000000',
  p_y_axis_major_interval  =>null,
  p_y_axis_minor_interval  =>null,
  p_async_update           =>'N',
  p_async_time             =>null,
  p_legend_title           =>'',
  p_legend_title_font      =>'',
  p_names_font             => null,
  p_names_rotation         => null,
  p_values_font            =>'Tahoma:10:#000000',
  p_values_rotation        =>null,
  p_values_prefix          =>'',
  p_values_postfix         =>'',
  p_hints_font             =>'Tahoma:10:#000000',
  p_legend_font            =>'Tahoma:10:#000000',
  p_grid_labels_font       =>'Tahoma:10:#000000',
  p_chart_title_font       =>'Tahoma:14:#000000',
  p_x_axis_title_font      =>'Tahoma:14:#000000',
  p_x_axis_title_rotation  =>'',
  p_y_axis_title_font      =>'Tahoma:14:#000000',
  p_y_axis_title_rotation  =>'',
  p_gauge_labels_font      =>'Tahoma:10:#000000',
  p_use_chart_xml          =>'N',
  p_chart_xml              => a1);
end;
/
declare
 a1 varchar2(32767) := null;
begin
a1:=a1||'select category_id link,'||unistr('\000a')||
'       pc.category_name label,'||unistr('\000a')||
'       (select count(1) from product_categories child '||unistr('\000a')||
'         where pc.category_id = child.parent_category_id) value'||unistr('\000a')||
'  from product_categories pc'||unistr('\000a')||
' where pc.parent_category_id = pc.category_id';

wwv_flow_api.create_flash_chart5_series(
  p_id                        => 2242830513309405+wwv_flow_api.g_id_offset,
  p_chart_id                  => 2242716028309399+wwv_flow_api.g_id_offset,
  p_flow_id                   => wwv_flow.g_flow_id,
  p_series_seq                =>10,
  p_series_name               =>'Series 1',
  p_series_query              => a1,
  p_series_type               =>'',
  p_series_query_type         =>'SQL_QUERY',
  p_series_ajax_items_to_submit=>'',
  p_series_query_parse_opt    =>'PARSE_CHART_QUERY',
  p_series_query_no_data_found=>'No data found.',
  p_series_query_row_count_max=>15,
  p_action_link               =>'f?p=&APP_ID.:2:&SESSION.::&DEBUG.:RP:P2_PARENT_CATEGORY_ID:#LINK#',
  p_show_action_link          =>'C',
  p_action_link_checksum_type =>'',
  p_display_when_cond_type    =>'',
  p_display_when_condition    =>'',
  p_display_when_condition2   =>'');
end;
/
 
begin
 
null;
 
end;
/

 
begin
 
null;
 
end;
/

 
begin
 
---------------------------------------
-- ...updatable report columns for page 1
--
 
begin
 
null;
end;
null;
 
end;
/

 
--application/pages/page_00002
prompt  ...PAGE 2: Category List
--
 
begin
 
wwv_flow_api.create_page (
  p_flow_id => wwv_flow.g_flow_id
 ,p_id => 2
 ,p_user_interface_id => 2227404414528941 + wwv_flow_api.g_id_offset
 ,p_name => 'Category List'
 ,p_step_title => 'Products'
 ,p_allow_duplicate_submissions => 'Y'
 ,p_step_sub_title => 'Products'
 ,p_step_sub_title_type => 'TEXT_WITH_SUBSTITUTIONS'
 ,p_include_apex_css_js_yn => 'Y'
 ,p_autocomplete_on_off => 'ON'
 ,p_page_is_public_y_n => 'Y'
 ,p_protection_level => 'N'
 ,p_cache_page_yn => 'N'
 ,p_cache_timeout_seconds => 21600
 ,p_cache_by_user_yn => 'N'
 ,p_help_text => 
'No help is available for this page.'
 ,p_last_updated_by => 'ADMIN'
 ,p_last_upd_yyyymmddhh24miss => '20150409131037'
  );
null;
 
end;
/

declare
  s varchar2(32767) := null;
  l_clob clob;
  l_length number := 1;
begin
s := null;
wwv_flow_api.create_page_plug (
  p_id=> 2234321863564926 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_page_id=> 2,
  p_plug_name=> 'Search',
  p_region_name=>'',
  p_escape_on_http_output=>'N',
  p_plug_template=> 0,
  p_plug_display_sequence=> 10,
  p_plug_new_grid         => false,
  p_plug_new_grid_row     => true,
  p_plug_new_grid_column  => true,
  p_plug_display_column=> null,
  p_plug_display_point=> 'BODY_3',
  p_plug_item_display_point=> 'ABOVE',
  p_plug_source=> s,
  p_plug_source_type=> 'STATIC_TEXT',
  p_translate_title=> 'Y',
  p_plug_query_row_template=> 1,
  p_plug_query_headings_type=> 'COLON_DELMITED_LIST',
  p_plug_query_row_count_max => 500,
  p_plug_display_condition_type => '',
  p_plug_customized=>'0',
  p_plug_caching=> 'NOT_CACHED',
  p_plug_comment=> '');
end;
/
declare
  s varchar2(32767) := null;
  l_clob clob;
  l_length number := 1;
begin
s:=s||'select'||unistr('\000a')||
' category_id,'||unistr('\000a')||
' CATEGORY_NAME'||unistr('\000a')||
'from PRODUCT_CATEGORIES'||unistr('\000a')||
'where parent_category_id = :P2_PARENT_CATEGORY_ID';

wwv_flow_api.create_report_region (
  p_id=> 2235925018564963 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_page_id=> 2,
  p_name=> 'Child Categories',
  p_region_name=>'',
  p_template=> 2225824916528837+ wwv_flow_api.g_id_offset,
  p_display_sequence=> 20,
  p_new_grid         => false,
  p_new_grid_row     => true,
  p_new_grid_column  => true,
  p_display_column=> null,
  p_display_point=> 'BODY_3',
  p_item_display_point=> 'ABOVE',
  p_source=> s,
  p_source_type=> 'SQL_QUERY',
  p_plug_caching=> 'NOT_CACHED',
  p_customized=> '0',
  p_translate_title=> 'Y',
  p_ajax_enabled=> 'N',
  p_query_row_template=> 2225901133528838+ wwv_flow_api.g_id_offset,
  p_query_headings_type=> 'COLON_DELMITED_LIST',
  p_query_num_rows=> '15',
  p_query_options=> 'DERIVED_REPORT_COLUMNS',
  p_query_break_cols=> '0',
  p_query_no_data_found=> 'No data found.',
  p_query_num_rows_item=> 'P2_ROWS',
  p_query_num_rows_type=> 'ROW_RANGES_IN_SELECT_LIST',
  p_query_row_count_max=> '500',
  p_pagination_display_position=> 'BOTTOM_RIGHT',
  p_break_type_flag=> 'DEFAULT_BREAK_FORMATTING',
  p_csv_output=> 'N',
  p_query_asc_image=> 'apex/builder/dup.gif',
  p_query_asc_image_attr=> 'width="16" height="16" alt="" ',
  p_query_desc_image=> 'apex/builder/ddown.gif',
  p_query_desc_image_attr=> 'width="16" height="16" alt="" ',
  p_plug_query_strip_html=> 'Y',
  p_comment=>'');
end;
/
declare
  s varchar2(32767) := null;
begin
s := null;
wwv_flow_api.create_report_columns (
  p_id=> 2247708103406431 + wwv_flow_api.g_id_offset,
  p_region_id=> 2235925018564963 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_query_column_id=> 1,
  p_form_element_id=> null,
  p_column_alias=> 'CATEGORY_ID',
  p_column_display_sequence=> 2,
  p_column_heading=> 'Category Id',
  p_column_alignment=>'LEFT',
  p_heading_alignment=>'CENTER',
  p_default_sort_column_sequence=>0,
  p_disable_sort_column=>'Y',
  p_sum_column=> 'N',
  p_hidden_column=> 'Y',
  p_display_as=>'ESCAPE_SC',
  p_is_required=> false,
  p_pk_col_source=> s,
  p_column_comment=>'');
end;
/
declare
  s varchar2(32767) := null;
begin
s := null;
wwv_flow_api.create_report_columns (
  p_id=> 2243306427346487 + wwv_flow_api.g_id_offset,
  p_region_id=> 2235925018564963 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_query_column_id=> 2,
  p_form_element_id=> null,
  p_column_alias=> 'CATEGORY_NAME',
  p_column_display_sequence=> 1,
  p_column_heading=> 'Category Name',
  p_use_as_row_header=> 'N',
  p_column_link=>'f?p=&APP_ID.:3:&SESSION.::&DEBUG.:RP:P3_CATEGORY_ID:#CATEGORY_ID#',
  p_column_linktext=>'#CATEGORY_NAME#',
  p_column_alignment=>'LEFT',
  p_heading_alignment=>'CENTER',
  p_default_sort_column_sequence=>0,
  p_disable_sort_column=>'Y',
  p_sum_column=> 'N',
  p_hidden_column=> 'N',
  p_display_as=>'ESCAPE_SC',
  p_lov_show_nulls=> 'NO',
  p_is_required=> false,
  p_pk_col_source=> s,
  p_lov_display_extra=> 'YES',
  p_include_in_export=> 'Y',
  p_column_comment=>'');
end;
/
 
begin
 
null;
 
end;
/

 
begin
 
wwv_flow_api.create_page_branch(
  p_id=>2235829965564941 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_flow_step_id=> 2,
  p_branch_name=> '',
  p_branch_action=> 'f?p=&APP_ID.:2:&SESSION.',
  p_branch_point=> 'AFTER_PROCESSING',
  p_branch_type=> 'REDIRECT_URL',
  p_branch_sequence=> 10,
  p_save_state_before_branch_yn=>'N',
  p_branch_comment=> '');
 
 
end;
/

declare
    h varchar2(32767) := null;
begin
wwv_flow_api.create_page_item(
  p_id=>2234611133564929 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_flow_step_id=> 2,
  p_name=>'P2_PARENT_CATEGORY_ID',
  p_data_type=> 'VARCHAR',
  p_is_required=> false,
  p_accept_processing=> 'REPLACE_EXISTING',
  p_item_sequence=> 10,
  p_item_plug_id => 2234321863564926+wwv_flow_api.g_id_offset,
  p_use_cache_before_default=> 'YES',
  p_item_default_type=> 'STATIC_TEXT_WITH_SUBSTITUTIONS',
  p_prompt=>'Search',
  p_source_type=> 'STATIC',
  p_display_as=> 'NATIVE_HIDDEN',
  p_lov_display_null=> 'NO',
  p_lov_translated=> 'N',
  p_cSize=> 30,
  p_cMaxlength=> 2000,
  p_cHeight=> 1,
  p_new_grid=> false,
  p_begin_on_new_line=> 'YES',
  p_begin_on_new_field=> 'YES',
  p_colspan=> null,
  p_rowspan=> null,
  p_grid_column=> null,
  p_label_alignment=> 'RIGHT',
  p_field_alignment=> 'LEFT',
  p_field_template=> 2226531044528882+wwv_flow_api.g_id_offset,
  p_is_persistent=> 'Y',
  p_lov_display_extra=>'YES',
  p_protection_level => 'N',
  p_escape_on_http_output => 'Y',
  p_attribute_01 => 'Y',
  p_show_quick_picks=>'N',
  p_item_comment => '');
 
 
end;
/

 
begin
 
---------------------------------------
-- ...updatable report columns for page 2
--
 
begin
 
null;
end;
null;
 
end;
/

 
--application/pages/page_00003
prompt  ...PAGE 3: Products
--
 
begin
 
wwv_flow_api.create_page (
  p_flow_id => wwv_flow.g_flow_id
 ,p_id => 3
 ,p_user_interface_id => 2227404414528941 + wwv_flow_api.g_id_offset
 ,p_name => 'Products'
 ,p_step_title => 'Products'
 ,p_allow_duplicate_submissions => 'Y'
 ,p_step_sub_title => 'Products'
 ,p_step_sub_title_type => 'TEXT_WITH_SUBSTITUTIONS'
 ,p_include_apex_css_js_yn => 'Y'
 ,p_autocomplete_on_off => 'ON'
 ,p_page_is_public_y_n => 'Y'
 ,p_protection_level => 'N'
 ,p_cache_page_yn => 'N'
 ,p_cache_timeout_seconds => 21600
 ,p_cache_by_user_yn => 'N'
 ,p_help_text => 
'No help is available for this page.'
 ,p_last_updated_by => 'ADMIN'
 ,p_last_upd_yyyymmddhh24miss => '20150409130950'
  );
null;
 
end;
/

declare
  s varchar2(32767) := null;
  l_clob clob;
  l_length number := 1;
begin
s:=s||'select '||unistr('\000a')||
' "PRODUCT_NAME",'||unistr('\000a')||
' "PRODUCT_STATUS",'||unistr('\000a')||
' "COST_PRICE",'||unistr('\000a')||
' "LIST_PRICE"'||unistr('\000a')||
'from #OWNER#.PRODUCTS'||unistr('\000a')||
'where category_id = :P3_CATEGORY_ID';

wwv_flow_api.create_report_region (
  p_id=> 2244500548379498 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_page_id=> 3,
  p_name=> 'Products',
  p_region_name=>'',
  p_template=> 2225824916528837+ wwv_flow_api.g_id_offset,
  p_display_sequence=> 10,
  p_new_grid         => false,
  p_new_grid_row     => true,
  p_new_grid_column  => true,
  p_display_column=> null,
  p_display_point=> 'BODY_3',
  p_item_display_point=> 'ABOVE',
  p_source=> s,
  p_source_type=> 'SQL_QUERY',
  p_plug_caching=> 'NOT_CACHED',
  p_customized=> '0',
  p_translate_title=> 'Y',
  p_ajax_enabled=> 'N',
  p_query_row_template=> 2225901133528838+ wwv_flow_api.g_id_offset,
  p_query_headings_type=> 'COLON_DELMITED_LIST',
  p_query_num_rows=> '15',
  p_query_options=> 'DERIVED_REPORT_COLUMNS',
  p_query_break_cols=> '0',
  p_query_no_data_found=> 'No data found.',
  p_query_num_rows_type=> 'ROW_RANGES_IN_SELECT_LIST',
  p_query_row_count_max=> '500',
  p_pagination_display_position=> 'BOTTOM_RIGHT',
  p_break_type_flag=> 'DEFAULT_BREAK_FORMATTING',
  p_csv_output=> 'N',
  p_query_asc_image=> 'apex/builder/dup.gif',
  p_query_asc_image_attr=> 'width="16" height="16" alt="" ',
  p_query_desc_image=> 'apex/builder/ddown.gif',
  p_query_desc_image_attr=> 'width="16" height="16" alt="" ',
  p_plug_query_strip_html=> 'Y',
  p_comment=>'');
end;
/
declare
  s varchar2(32767) := null;
begin
s := null;
wwv_flow_api.create_report_columns (
  p_id=> 2245105964379504 + wwv_flow_api.g_id_offset,
  p_region_id=> 2244500548379498 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_query_column_id=> 1,
  p_form_element_id=> null,
  p_column_alias=> 'PRODUCT_NAME',
  p_column_display_sequence=> 1,
  p_column_heading=> 'PRODUCT_NAME',
  p_column_alignment=>'LEFT',
  p_default_sort_column_sequence=>0,
  p_disable_sort_column=>'N',
  p_sum_column=> 'N',
  p_hidden_column=> 'N',
  p_display_as=>'ESCAPE_SC',
  p_is_required=> false,
  p_pk_col_source=> s,
  p_column_comment=>'');
end;
/
declare
  s varchar2(32767) := null;
begin
s := null;
wwv_flow_api.create_report_columns (
  p_id=> 2245208641379504 + wwv_flow_api.g_id_offset,
  p_region_id=> 2244500548379498 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_query_column_id=> 2,
  p_form_element_id=> null,
  p_column_alias=> 'PRODUCT_STATUS',
  p_column_display_sequence=> 2,
  p_column_heading=> 'PRODUCT_STATUS',
  p_column_alignment=>'LEFT',
  p_default_sort_column_sequence=>0,
  p_disable_sort_column=>'N',
  p_sum_column=> 'N',
  p_hidden_column=> 'N',
  p_display_as=>'ESCAPE_SC',
  p_is_required=> false,
  p_pk_col_source=> s,
  p_column_comment=>'');
end;
/
declare
  s varchar2(32767) := null;
begin
s := null;
wwv_flow_api.create_report_columns (
  p_id=> 2245316019379504 + wwv_flow_api.g_id_offset,
  p_region_id=> 2244500548379498 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_query_column_id=> 3,
  p_form_element_id=> null,
  p_column_alias=> 'COST_PRICE',
  p_column_display_sequence=> 3,
  p_column_heading=> 'COST_PRICE',
  p_column_alignment=>'LEFT',
  p_default_sort_column_sequence=>0,
  p_disable_sort_column=>'N',
  p_sum_column=> 'N',
  p_hidden_column=> 'N',
  p_display_as=>'ESCAPE_SC',
  p_is_required=> false,
  p_pk_col_source=> s,
  p_column_comment=>'');
end;
/
declare
  s varchar2(32767) := null;
begin
s := null;
wwv_flow_api.create_report_columns (
  p_id=> 2245423990379504 + wwv_flow_api.g_id_offset,
  p_region_id=> 2244500548379498 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_query_column_id=> 4,
  p_form_element_id=> null,
  p_column_alias=> 'LIST_PRICE',
  p_column_display_sequence=> 4,
  p_column_heading=> 'LIST_PRICE',
  p_column_alignment=>'LEFT',
  p_default_sort_column_sequence=>0,
  p_disable_sort_column=>'N',
  p_sum_column=> 'N',
  p_hidden_column=> 'N',
  p_display_as=>'ESCAPE_SC',
  p_is_required=> false,
  p_pk_col_source=> s,
  p_column_comment=>'');
end;
/
 
begin
 
null;
 
end;
/

 
begin
 
null;
 
end;
/

declare
    h varchar2(32767) := null;
begin
wwv_flow_api.create_page_item(
  p_id=>2247419964400944 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_flow_step_id=> 3,
  p_name=>'P3_CATEGORY_ID',
  p_data_type=> 'VARCHAR',
  p_is_required=> false,
  p_accept_processing=> 'REPLACE_EXISTING',
  p_item_sequence=> 10,
  p_item_plug_id => 2244500548379498+wwv_flow_api.g_id_offset,
  p_use_cache_before_default=> 'YES',
  p_item_default_type=> 'STATIC_TEXT_WITH_SUBSTITUTIONS',
  p_source_type=> 'STATIC',
  p_display_as=> 'NATIVE_HIDDEN',
  p_lov_display_null=> 'NO',
  p_lov_translated=> 'N',
  p_cSize=> null,
  p_cMaxlength=> 4000,
  p_cHeight=> null,
  p_new_grid=> false,
  p_begin_on_new_line=> 'YES',
  p_begin_on_new_field=> 'YES',
  p_colspan=> null,
  p_rowspan=> null,
  p_grid_column=> null,
  p_label_alignment=> 'RIGHT',
  p_field_alignment=> 'LEFT-CENTER',
  p_is_persistent=> 'Y',
  p_attribute_01 => 'Y',
  p_item_comment => '');
 
 
end;
/

 
begin
 
---------------------------------------
-- ...updatable report columns for page 3
--
 
begin
 
null;
end;
null;
 
end;
/

 
--application/pages/page_00101
prompt  ...PAGE 101: Login
--
 
begin
 
wwv_flow_api.create_page (
  p_flow_id => wwv_flow.g_flow_id
 ,p_id => 101
 ,p_user_interface_id => 2227404414528941 + wwv_flow_api.g_id_offset
 ,p_name => 'Login'
 ,p_alias => 'LOGIN_JQM_SMARTPHONE'
 ,p_step_title => 'Login'
 ,p_step_sub_title_type => 'TEXT_WITH_SUBSTITUTIONS'
 ,p_first_item => 'AUTO_FIRST_ITEM'
 ,p_include_apex_css_js_yn => 'Y'
 ,p_autocomplete_on_off => 'OFF'
 ,p_page_is_public_y_n => 'Y'
 ,p_cache_page_yn => 'N'
 ,p_last_upd_yyyymmddhh24miss => '20150409104406'
  );
null;
 
end;
/

declare
  s varchar2(32767) := null;
  l_clob clob;
  l_length number := 1;
begin
s := null;
wwv_flow_api.create_page_plug (
  p_id=> 2227923594529009 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_page_id=> 101,
  p_plug_name=> 'Login',
  p_region_name=>'',
  p_escape_on_http_output=>'N',
  p_plug_template=> 2225601475528836+ wwv_flow_api.g_id_offset,
  p_plug_display_sequence=> 10,
  p_plug_new_grid         => false,
  p_plug_new_grid_row     => true,
  p_plug_new_grid_column  => true,
  p_plug_display_column=> null,
  p_plug_display_point=> 'BODY_3',
  p_plug_item_display_point=> 'ABOVE',
  p_plug_source=> s,
  p_plug_source_type=> 'STATIC_TEXT',
  p_plug_query_row_template=> 1,
  p_plug_query_headings_type=> 'COLON_DELMITED_LIST',
  p_plug_query_row_count_max => 500,
  p_plug_display_condition_type => '',
  p_plug_caching=> 'NOT_CACHED',
  p_plug_comment=> '');
end;
/
 
begin
 
null;
 
end;
/

 
begin
 
null;
 
end;
/

declare
    h varchar2(32767) := null;
begin
wwv_flow_api.create_page_item(
  p_id=>2228014544529016 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_flow_step_id=> 101,
  p_name=>'P101_USERNAME',
  p_data_type=> '',
  p_is_required=> false,
  p_accept_processing=> 'REPLACE_EXISTING',
  p_item_sequence=> 10,
  p_item_plug_id => 2227923594529009+wwv_flow_api.g_id_offset,
  p_use_cache_before_default=> '',
  p_prompt=>'Username',
  p_display_as=> 'NATIVE_TEXT_FIELD',
  p_lov_display_null=> 'NO',
  p_lov_translated=> 'N',
  p_cSize=> 40,
  p_cMaxlength=> 100,
  p_cHeight=> null,
  p_new_grid=> false,
  p_begin_on_new_line=> 'YES',
  p_begin_on_new_field=> 'YES',
  p_colspan=> null,
  p_rowspan=> null,
  p_grid_column=> null,
  p_label_alignment=> 'RIGHT',
  p_field_alignment=> 'LEFT',
  p_field_template=> 2226531044528882+wwv_flow_api.g_id_offset,
  p_is_persistent=> 'Y',
  p_attribute_01 => 'N',
  p_attribute_02 => 'N',
  p_attribute_03 => 'N',
  p_attribute_04 => 'TEXT',
  p_item_comment => '');
 
 
end;
/

declare
    h varchar2(32767) := null;
begin
wwv_flow_api.create_page_item(
  p_id=>2228118770529029 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_flow_step_id=> 101,
  p_name=>'P101_PASSWORD',
  p_data_type=> '',
  p_is_required=> false,
  p_accept_processing=> 'REPLACE_EXISTING',
  p_item_sequence=> 20,
  p_item_plug_id => 2227923594529009+wwv_flow_api.g_id_offset,
  p_use_cache_before_default=> '',
  p_prompt=>'Password',
  p_display_as=> 'NATIVE_PASSWORD',
  p_lov_display_null=> 'NO',
  p_lov_translated=> 'N',
  p_cSize=> 40,
  p_cMaxlength=> 100,
  p_cHeight=> null,
  p_new_grid=> false,
  p_begin_on_new_line=> 'YES',
  p_begin_on_new_field=> 'YES',
  p_colspan=> null,
  p_rowspan=> null,
  p_grid_column=> null,
  p_label_alignment=> 'RIGHT',
  p_field_alignment=> 'LEFT',
  p_field_template=> 2226531044528882+wwv_flow_api.g_id_offset,
  p_is_persistent=> 'Y',
  p_attribute_01 => 'Y',
  p_attribute_02 => 'Y',
  p_item_comment => '');
 
 
end;
/

declare
    h varchar2(32767) := null;
begin
wwv_flow_api.create_page_item(
  p_id=>2228231396529034 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_flow_step_id=> 101,
  p_name=>'P101_LOGIN',
  p_data_type=> '',
  p_is_required=> false,
  p_accept_processing=> 'REPLACE_EXISTING',
  p_item_sequence=> 30,
  p_item_plug_id => 2227923594529009+wwv_flow_api.g_id_offset,
  p_use_cache_before_default=> '',
  p_item_default=> 'Login',
  p_prompt=>'Login',
  p_source=>'LOGIN',
  p_source_type=> 'STATIC',
  p_display_as=> 'BUTTON',
  p_lov_display_null=> 'NO',
  p_lov_translated=> 'N',
  p_cSize=> null,
  p_cMaxlength=> null,
  p_cHeight=> null,
  p_tag_attributes  => 'template:'||to_char(2226903194528893 + wwv_flow_api.g_id_offset),
  p_new_grid=> false,
  p_begin_on_new_line=> 'YES',
  p_begin_on_new_field=> 'YES',
  p_colspan=> null,
  p_rowspan=> null,
  p_grid_column=> null,
  p_label_alignment=> 'LEFT',
  p_field_alignment=> 'LEFT',
  p_is_persistent=> 'Y',
  p_button_action => 'SUBMIT',
  p_button_is_hot=>'Y',
  p_item_comment => '');
 
 
end;
/

 
begin
 
declare
  p varchar2(32767) := null;
  l_clob clob;
  l_length number := 1;
begin
p:=p||'apex_authentication.send_login_username_cookie ('||unistr('\000a')||
'    p_username => lower(:P101_USERNAME) );';

wwv_flow_api.create_page_process(
  p_id     => 2228427692529044 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_flow_step_id => 101,
  p_process_sequence=> 10,
  p_process_point=> 'AFTER_SUBMIT',
  p_process_type=> 'PLSQL',
  p_process_name=> 'Set Username Cookie',
  p_process_sql_clob => p,
  p_process_error_message=> '',
  p_error_display_location=> 'INLINE_IN_NOTIFICATION',
  p_process_success_message=> '',
  p_process_is_stateful_y_n=>'N',
  p_process_comment=>'');
end;
null;
 
end;
/

 
begin
 
declare
  p varchar2(32767) := null;
  l_clob clob;
  l_length number := 1;
begin
p:=p||'apex_authentication.login('||unistr('\000a')||
'    p_username => :P101_USERNAME,'||unistr('\000a')||
'    p_password => :P101_PASSWORD );';

wwv_flow_api.create_page_process(
  p_id     => 2228313149529035 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_flow_step_id => 101,
  p_process_sequence=> 20,
  p_process_point=> 'AFTER_SUBMIT',
  p_process_type=> 'PLSQL',
  p_process_name=> 'Login',
  p_process_sql_clob => p,
  p_process_error_message=> '',
  p_error_display_location=> 'INLINE_IN_NOTIFICATION',
  p_process_success_message=> '',
  p_process_is_stateful_y_n=>'N',
  p_process_comment=>'');
end;
null;
 
end;
/

 
begin
 
declare
  p varchar2(32767) := null;
  l_clob clob;
  l_length number := 1;
begin
p:=p||'101';

wwv_flow_api.create_page_process(
  p_id     => 2228606902529045 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_flow_step_id => 101,
  p_process_sequence=> 30,
  p_process_point=> 'AFTER_SUBMIT',
  p_process_type=> 'CLEAR_CACHE_FOR_PAGES',
  p_process_name=> 'Clear Page(s) Cache',
  p_process_sql_clob => p,
  p_process_error_message=> '',
  p_error_display_location=> 'INLINE_IN_NOTIFICATION',
  p_process_success_message=> '',
  p_process_is_stateful_y_n=>'N',
  p_process_comment=>'');
end;
null;
 
end;
/

 
begin
 
declare
  p varchar2(32767) := null;
  l_clob clob;
  l_length number := 1;
begin
p:=p||':P101_USERNAME := apex_authentication.get_login_username_cookie;';

wwv_flow_api.create_page_process(
  p_id     => 2228506315529044 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_flow_step_id => 101,
  p_process_sequence=> 10,
  p_process_point=> 'BEFORE_HEADER',
  p_process_type=> 'PLSQL',
  p_process_name=> 'Get Username Cookie',
  p_process_sql_clob => p,
  p_process_error_message=> '',
  p_error_display_location=> 'ON_ERROR_PAGE',
  p_process_success_message=> '',
  p_process_is_stateful_y_n=>'N',
  p_process_comment=>'');
end;
null;
 
end;
/

 
begin
 
---------------------------------------
-- ...updatable report columns for page 101
--
 
begin
 
null;
end;
null;
 
end;
/

prompt  ...lists
--
--application/shared_components/navigation/breadcrumbs
prompt  ...breadcrumbs
--
 
begin
 
wwv_flow_api.create_menu (
  p_id=> 2228711957529045 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_name=> ' Breadcrumb');
 
null;
 
end;
/

prompt  ...page templates for application: 101
--
--application/shared_components/user_interface/templates/page/page
prompt  ......Page template 2224121284528793
 
begin
 
wwv_flow_api.create_template (
  p_id => 2224121284528793 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_theme_id => 50
 ,p_name => 'Page'
 ,p_is_popup => false
 ,p_css_file_urls => '#IMAGE_PREFIX#themes/theme_50/css/4_2.css'
 ,p_header_template => '<!DOCTYPE html>'||unistr('\000a')||
'<html lang="&BROWSER_LANGUAGE.">'||unistr('\000a')||
'<head>'||unistr('\000a')||
'  <meta charset="utf-8" />'||unistr('\000a')||
'  <meta name="apple-mobile-web-app-capable" content="yes">'||unistr('\000a')||
'  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scaleable=no">'||unistr('\000a')||
'  <title>#TITLE#</title>'||unistr('\000a')||
'  #APEX_CSS#'||unistr('\000a')||
'  #TEMPLATE_CSS#'||unistr('\000a')||
'  #APEX_JAVASCRIPT#'||unistr('\000a')||
'  #TEMPLATE_JAVASCRIPT#'||unistr('\000a')||
'  #APPLICATION_JAVASCRIPT#'||unistr('\000a')||
'  #HEAD#'||unistr('\000a')||
'</head>'||unistr('\000a')||
'<body #ONLOAD#>'
 ,p_box => 
'<div id="#PAGE_STATIC_ID#" data-role="page" data-apex-page-transition="#PAGE_TRANSITION#" data-apex-popup-transition="#POPUP_TRANSITION#" data-theme="c" data-url="#REQUESTED_URL#">'||unistr('\000a')||
'#PAGE_CSS#'||unistr('\000a')||
'#FORM_OPEN#'||unistr('\000a')||
''||unistr('\000a')||
'#REGION_POSITION_01#'||unistr('\000a')||
''||unistr('\000a')||
'<div data-role="content" data-theme="c">'||unistr('\000a')||
'  <div id="messages">'||unistr('\000a')||
'    #SUCCESS_MESSAGE##NOTIFICATION_MESSAGE##GLOBAL_NOTIFICATION#'||unistr('\000a')||
'  </div>'||unistr('\000a')||
'  #BOX_BODY#'||unistr('\000a')||
'</div><!-- /content -->'||
''||unistr('\000a')||
''||unistr('\000a')||
'#REGION_POSITION_08#'||unistr('\000a')||
''||unistr('\000a')||
'#FORM_CLOSE#'||unistr('\000a')||
'#PAGE_JAVASCRIPT#'||unistr('\000a')||
'#GENERATED_CSS#'||unistr('\000a')||
'#GENERATED_JAVASCRIPT#'||unistr('\000a')||
'</div><!-- /page -->'
 ,p_footer_template => 
'</body>'||unistr('\000a')||
'</html>'
 ,p_success_message => '<div class="ui-bar ui-bar-d success" style="margin-bottom:20px;" id="success-message">'||unistr('\000a')||
'  <h4 style="display:inline-block;margin-top:5px;">#SUCCESS_MESSAGE#</h4>'||unistr('\000a')||
'</div>'
 ,p_notification_message => '<div class="ui-bar ui-bar-b notification" id="notification-message">'||unistr('\000a')||
'  <h4 style="display:inline-block; margin-top:5px;">#MESSAGE#</h4>'||unistr('\000a')||
'</div>'
 ,p_theme_class_id => 17
 ,p_grid_type => 'VARIABLE'
 ,p_grid_max_columns => 5
 ,p_grid_always_use_max_columns => false
 ,p_grid_has_column_span => false
 ,p_grid_emit_empty_leading_cols => true
 ,p_grid_emit_empty_trail_cols => false
 ,p_grid_template => '<div class="ui-grid-#USED_COLUMNS_ALPHA_MINUS# apex-grid-debug">#ROWS#</div>'
 ,p_grid_row_template => '#COLUMNS#'
 ,p_grid_column_template => '<div class="ui-block-#COLUMN_ALPHA#">#CONTENT#</div>'
 ,p_grid_javascript_debug_code => 'apex.jQuery(document).on("apex-devbar-grid-debug-on", function(){'||unistr('\000a')||
'    apex.jQuery(".apex-grid-debug > [class*=''ui-block-'']").addClass("ui-bar-e"); });'||unistr('\000a')||
''||unistr('\000a')||
'apex.jQuery(document).on("apex-devbar-grid-debug-off", function(){'||unistr('\000a')||
'    apex.jQuery(".apex-grid-debug > [class*=''ui-block-'']").removeClass("ui-bar-e"); });'||unistr('\000a')||
''
 ,p_has_edit_links => false
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/page/popup
prompt  ......Page template 2224227739528803
 
begin
 
wwv_flow_api.create_template (
  p_id => 2224227739528803 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_theme_id => 50
 ,p_name => 'Popup'
 ,p_is_popup => false
 ,p_css_file_urls => '#IMAGE_PREFIX#themes/theme_50/css/4_2.css'
 ,p_header_template => '<!DOCTYPE html>'||unistr('\000a')||
'<html lang="&BROWSER_LANGUAGE.">'||unistr('\000a')||
'<head>'||unistr('\000a')||
'  <meta charset="utf-8" />'||unistr('\000a')||
'  <title>#TITLE#</title>'||unistr('\000a')||
'  #APEX_CSS#'||unistr('\000a')||
'  #TEMPLATE_CSS#'||unistr('\000a')||
'  #APEX_JAVASCRIPT#'||unistr('\000a')||
'  #TEMPLATE_JAVASCRIPT#'||unistr('\000a')||
'  #APPLICATION_JAVASCRIPT#'||unistr('\000a')||
'  #HEAD#'||unistr('\000a')||
'</head>'||unistr('\000a')||
'<body #ONLOAD#>'
 ,p_box => 
'<div id="#PAGE_STATIC_ID#" data-role="page" data-apex-page-transition="#PAGE_TRANSITION#" data-apex-popup-transition="#POPUP_TRANSITION#" data-theme="c">'||unistr('\000a')||
'#PAGE_CSS#'||unistr('\000a')||
'#FORM_OPEN#'||unistr('\000a')||
''||unistr('\000a')||
'<div data-role="content" data-theme="b">'||unistr('\000a')||
'  <div id="messages">'||unistr('\000a')||
'    #SUCCESS_MESSAGE##NOTIFICATION_MESSAGE##GLOBAL_NOTIFICATION#'||unistr('\000a')||
'  </div>'||unistr('\000a')||
'  #BOX_BODY#'||unistr('\000a')||
'</div><!-- /content -->'||unistr('\000a')||
'#FORM_CLOSE#'||unistr('\000a')||
'#PAGE_JAVASCRIPT#'||unistr('\000a')||
'#GENERATED_CSS#'||unistr('\000a')||
'#'||
'GENERATED_JAVASCRIPT#'||unistr('\000a')||
'</div><!-- /page -->'||unistr('\000a')||
''
 ,p_footer_template => 
'</body>'||unistr('\000a')||
'</html>'
 ,p_success_message => '<div class="ui-bar ui-bar-d success" id="success-message">'||unistr('\000a')||
'  <h4 style="display:inline-block;margin-top:5px;">#SUCCESS_MESSAGE#</h4>'||unistr('\000a')||
'</div>'
 ,p_notification_message => '<div class="ui-bar ui-bar-b notification" id="notification-message">'||unistr('\000a')||
'  <h4 style="display:inline-block; margin-top:5px;">#MESSAGE#</h4>'||unistr('\000a')||
'</div>'
 ,p_theme_class_id => 17
 ,p_grid_type => 'VARIABLE'
 ,p_grid_max_columns => 5
 ,p_grid_always_use_max_columns => false
 ,p_grid_has_column_span => false
 ,p_grid_emit_empty_leading_cols => true
 ,p_grid_emit_empty_trail_cols => false
 ,p_grid_template => '<div class="ui-grid-#USED_COLUMNS_ALPHA_MINUS#">#ROWS#</div>'
 ,p_grid_row_template => '#COLUMNS#'
 ,p_grid_column_template => '<div class="ui-block-#COLUMN_ALPHA#">#CONTENT#</div>'
 ,p_grid_javascript_debug_code => 'apex.jQuery(document).on("apex-devbar-grid-debug-on", function(){'||unistr('\000a')||
'    apex.jQuery(".apex-grid-debug > [class*=''ui-block-'']").addClass("ui-bar-e"); });'||unistr('\000a')||
''||unistr('\000a')||
'apex.jQuery(document).on("apex-devbar-grid-debug-off", function(){'||unistr('\000a')||
'    apex.jQuery(".apex-grid-debug > [class*=''ui-block-'']").removeClass("ui-bar-e"); });'||unistr('\000a')||
''
 ,p_has_edit_links => false
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

prompt  ...button templates
--
--application/shared_components/user_interface/templates/button/100_button
prompt  ......Button Template 2226705972528884
 
begin
 
wwv_flow_api.create_button_templates (
  p_id => 2226705972528884 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_template_name => '100% Button'
 ,p_template => 
'<button id="#BUTTON_ID#" type="button" onclick="#JAVASCRIPT#" class="#BUTTON_CSS_CLASSES#" #BUTTON_ATTRIBUTES#>#LABEL#</button>'
 ,p_hot_template => 
'<button id="#BUTTON_ID#" type="button" data-theme="b" onclick="#JAVASCRIPT#" class="#BUTTON_CSS_CLASSES#" #BUTTON_ATTRIBUTES#>#LABEL#</button>'
 ,p_translate_this_template => 'N'
 ,p_theme_class_id => 1
 ,p_theme_id => 50
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/button/header_button
prompt  ......Button Template 2226817914528893
 
begin
 
wwv_flow_api.create_button_templates (
  p_id => 2226817914528893 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_template_name => 'Header Button'
 ,p_template => 
'<a href="#LINK#" id="#BUTTON_ID#" class="#BUTTON_CSS_CLASSES#" #BUTTON_ATTRIBUTES#>#LABEL#</a>'
 ,p_hot_template => 
'<a href="#LINK#" id="#BUTTON_ID#" class="#BUTTON_CSS_CLASSES#" data-theme="b" #BUTTON_ATTRIBUTES#>#LABEL#</a>'
 ,p_translate_this_template => 'N'
 ,p_theme_class_id => 1
 ,p_template_comment => 'It looks like that the <button> tag when used in the header creates a bigger header bar! Have to investigate that'
 ,p_theme_id => 50
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/button/inline_button
prompt  ......Button Template 2226903194528893
 
begin
 
wwv_flow_api.create_button_templates (
  p_id => 2226903194528893 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_template_name => 'Inline Button'
 ,p_template => 
'<button id="#BUTTON_ID#" type="button" data-inline="true" onclick="#JAVASCRIPT#" class="#BUTTON_CSS_CLASSES#" #BUTTON_ATTRIBUTES#>#LABEL#</button>'
 ,p_hot_template => 
'<button id="#BUTTON_ID#" type="button" data-inline="true" data-theme="b" onclick="#JAVASCRIPT#" class="#BUTTON_CSS_CLASSES#" #BUTTON_ATTRIBUTES#>#LABEL#</button>'
 ,p_translate_this_template => 'N'
 ,p_theme_class_id => 1
 ,p_theme_id => 50
  );
null;
 
end;
/

---------------------------------------
prompt  ...region templates
--
--application/shared_components/user_interface/templates/region/button_group_horizontal
prompt  ......region template 2224324198528812
 
begin
 
wwv_flow_api.create_plug_template (
  p_id => 2224324198528812 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_layout => 'TABLE'
 ,p_template => 
'<div id="#REGION_STATIC_ID#" data-role="controlgroup" data-type="horizontal" class="#REGION_CSS_CLASSES#" #REGION_ATTRIBUTES#>'||unistr('\000a')||
'#CLOSE#'||unistr('\000a')||
'#DELETE#'||unistr('\000a')||
'#CREATE#'||unistr('\000a')||
'#EDIT#'||unistr('\000a')||
'#CHANGE#'||unistr('\000a')||
'#BODY#'||unistr('\000a')||
'</div>'
 ,p_page_plug_template_name => 'Button Group (Horizontal)'
 ,p_theme_id => 50
 ,p_theme_class_id => 17
 ,p_default_label_alignment => 'RIGHT'
 ,p_default_field_alignment => 'LEFT'
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/region/button_group_vertical
prompt  ......region template 2224426588528830
 
begin
 
wwv_flow_api.create_plug_template (
  p_id => 2224426588528830 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_layout => 'TABLE'
 ,p_template => 
'<div id="#REGION_STATIC_ID#" data-role="controlgroup" class="#REGION_CSS_CLASSES#" #REGION_ATTRIBUTES#>'||unistr('\000a')||
'#CLOSE#'||unistr('\000a')||
'#DELETE#'||unistr('\000a')||
'#CREATE#'||unistr('\000a')||
'#EDIT#'||unistr('\000a')||
'#CHANGE#'||unistr('\000a')||
'#BODY#'||unistr('\000a')||
'</div>'
 ,p_page_plug_template_name => 'Button Group (Vertical)'
 ,p_theme_id => 50
 ,p_theme_class_id => 17
 ,p_default_label_alignment => 'RIGHT'
 ,p_default_field_alignment => 'LEFT'
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/region/collapsible_set_normal
prompt  ......region template 2224514883528832
 
begin
 
wwv_flow_api.create_plug_template (
  p_id => 2224514883528832 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_layout => 'TABLE'
 ,p_template => 
'<div id="#REGION_STATIC_ID#" data-role="collapsible-set" class="#REGION_CSS_CLASSES#" #REGION_ATTRIBUTES#>'||unistr('\000a')||
'#SUB_REGIONS#'||unistr('\000a')||
'</div>'
 ,p_page_plug_template_name => 'Collapsible Set-Normal'
 ,p_theme_id => 50
 ,p_theme_class_id => 1
 ,p_default_label_alignment => 'RIGHT'
 ,p_default_field_alignment => 'LEFT'
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/region/collapsible_set_smaller
prompt  ......region template 2224626936528832
 
begin
 
wwv_flow_api.create_plug_template (
  p_id => 2224626936528832 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_layout => 'TABLE'
 ,p_template => 
'<div id="#REGION_STATIC_ID#" data-role="collapsible-set" data-mini="true" class="#REGION_CSS_CLASSES#" #REGION_ATTRIBUTES#>'||unistr('\000a')||
'#SUB_REGIONS#'||unistr('\000a')||
'</div>'
 ,p_page_plug_template_name => 'Collapsible Set-Smaller'
 ,p_theme_id => 50
 ,p_theme_class_id => 1
 ,p_default_label_alignment => 'RIGHT'
 ,p_default_field_alignment => 'LEFT'
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/region/collapsible_normal_closed
prompt  ......region template 2224722272528832
 
begin
 
wwv_flow_api.create_plug_template (
  p_id => 2224722272528832 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_layout => 'TABLE'
 ,p_template => 
'<div id="#REGION_STATIC_ID#" data-role="collapsible" class="#REGION_CSS_CLASSES#" #REGION_ATTRIBUTES#>'||unistr('\000a')||
'<h3>#TITLE#</h3>'||unistr('\000a')||
'#BODY#'||unistr('\000a')||
'</div>'
 ,p_page_plug_template_name => 'Collapsible-Normal (Closed)'
 ,p_theme_id => 50
 ,p_theme_class_id => 1
 ,p_default_label_alignment => 'RIGHT'
 ,p_default_field_alignment => 'LEFT'
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/region/collapsible_normal_open
prompt  ......region template 2224823571528832
 
begin
 
wwv_flow_api.create_plug_template (
  p_id => 2224823571528832 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_layout => 'TABLE'
 ,p_template => 
'<div id="#REGION_STATIC_ID#" data-role="collapsible" data-collapsed="false" class="#REGION_CSS_CLASSES#" #REGION_ATTRIBUTES#>'||unistr('\000a')||
'<h3>#TITLE#</h3>'||unistr('\000a')||
'#BODY#'||unistr('\000a')||
'</div>'
 ,p_page_plug_template_name => 'Collapsible-Normal (Open)'
 ,p_theme_id => 50
 ,p_theme_class_id => 1
 ,p_default_label_alignment => 'RIGHT'
 ,p_default_field_alignment => 'LEFT'
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/region/collapsible_smaller_closed
prompt  ......region template 2224906177528832
 
begin
 
wwv_flow_api.create_plug_template (
  p_id => 2224906177528832 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_layout => 'TABLE'
 ,p_template => 
'<div id="#REGION_STATIC_ID#" data-role="collapsible" data-mini="true" class="#REGION_CSS_CLASSES#" #REGION_ATTRIBUTES#>'||unistr('\000a')||
'<h3>#TITLE#</h3>'||unistr('\000a')||
'#BODY#'||unistr('\000a')||
'</div>'
 ,p_page_plug_template_name => 'Collapsible-Smaller (Closed)'
 ,p_theme_id => 50
 ,p_theme_class_id => 1
 ,p_default_label_alignment => 'RIGHT'
 ,p_default_field_alignment => 'LEFT'
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/region/collapsible_smaller_open
prompt  ......region template 2225028825528832
 
begin
 
wwv_flow_api.create_plug_template (
  p_id => 2225028825528832 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_layout => 'TABLE'
 ,p_template => 
'<div id="#REGION_STATIC_ID#" data-role="collapsible" data-collapsed="false" data-mini="true" class="#REGION_CSS_CLASSES#" #REGION_ATTRIBUTES#>'||unistr('\000a')||
'<h3>#TITLE#</h3>'||unistr('\000a')||
'#BODY#'||unistr('\000a')||
'</div>'
 ,p_page_plug_template_name => 'Collapsible-Smaller (Open)'
 ,p_theme_id => 50
 ,p_theme_class_id => 1
 ,p_default_label_alignment => 'RIGHT'
 ,p_default_field_alignment => 'LEFT'
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/region/footer_toolbar_displays_at_bottom_of_page
prompt  ......region template 2225105959528832
 
begin
 
wwv_flow_api.create_plug_template (
  p_id => 2225105959528832 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_layout => 'TABLE'
 ,p_template => 
'<div id="#REGION_STATIC_ID#" data-role="footer" data-theme="b" class="#REGION_CSS_CLASSES#" #REGION_ATTRIBUTES#>'||unistr('\000a')||
'  #CHANGE#'||unistr('\000a')||
'  #BODY#'||unistr('\000a')||
'</div> <!-- /footer -->'
 ,p_page_plug_template_name => 'Footer Toolbar (Displays at Bottom of Page)'
 ,p_theme_id => 50
 ,p_theme_class_id => 22
 ,p_default_label_alignment => 'RIGHT'
 ,p_default_field_alignment => 'LEFT'
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/region/footer_toolbar_fixed
prompt  ......region template 2225211651528835
 
begin
 
wwv_flow_api.create_plug_template (
  p_id => 2225211651528835 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_layout => 'TABLE'
 ,p_template => 
'<div id="#REGION_STATIC_ID#" data-id="#REGION_STATIC_ID#" data-role="footer" data-theme="b" data-position="fixed" class="#REGION_CSS_CLASSES#" #REGION_ATTRIBUTES#>'||unistr('\000a')||
'  #CHANGE#'||unistr('\000a')||
'  #BODY#'||unistr('\000a')||
'</div> <!-- /footer -->'
 ,p_page_plug_template_name => 'Footer Toolbar (Fixed)'
 ,p_theme_id => 50
 ,p_theme_class_id => 22
 ,p_default_label_alignment => 'RIGHT'
 ,p_default_field_alignment => 'LEFT'
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/region/footer_toolbar_fullscreen
prompt  ......region template 2225329043528835
 
begin
 
wwv_flow_api.create_plug_template (
  p_id => 2225329043528835 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_layout => 'TABLE'
 ,p_template => 
'<div id="#REGION_STATIC_ID#" data-id="#REGION_STATIC_ID#" data-role="footer" data-theme="b" data-position="fixed" data-fullscreen="true" class="#REGION_CSS_CLASSES#" #REGION_ATTRIBUTES#>'||unistr('\000a')||
'  #CHANGE#'||unistr('\000a')||
'  #BODY#'||unistr('\000a')||
'</div> <!-- /footer -->'
 ,p_page_plug_template_name => 'Footer Toolbar (Fullscreen)'
 ,p_theme_id => 50
 ,p_theme_class_id => 22
 ,p_default_label_alignment => 'RIGHT'
 ,p_default_field_alignment => 'LEFT'
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/region/header_toolbar_fixed
prompt  ......region template 2225409203528836
 
begin
 
wwv_flow_api.create_plug_template (
  p_id => 2225409203528836 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_layout => 'TABLE'
 ,p_template => 
'<div id="#REGION_STATIC_ID#" data-id="#REGION_STATIC_ID#" data-role="header" data-theme="b" data-position="fixed" class="#REGION_CSS_CLASSES#" #REGION_ATTRIBUTES#>'||unistr('\000a')||
'  #PREVIOUS#'||unistr('\000a')||
'  <h1>#PAGE_TITLE#</h1>'||unistr('\000a')||
'  #NEXT#'||unistr('\000a')||
'  #BODY#'||unistr('\000a')||
'</div> <!-- /header -->'
 ,p_page_plug_template_name => 'Header Toolbar (Fixed)'
 ,p_theme_id => 50
 ,p_theme_class_id => 21
 ,p_default_label_alignment => 'RIGHT'
 ,p_default_field_alignment => 'LEFT'
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/region/header_toolbar_fullscreen
prompt  ......region template 2225520481528836
 
begin
 
wwv_flow_api.create_plug_template (
  p_id => 2225520481528836 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_layout => 'TABLE'
 ,p_template => 
'<div id="#REGION_STATIC_ID#" data-id="#REGION_STATIC_ID#" data-role="header" data-theme="b" data-position="fixed" data-fullscreen="true" class="#REGION_CSS_CLASSES#" #REGION_ATTRIBUTES#>'||unistr('\000a')||
'  #PREVIOUS#'||unistr('\000a')||
'  <h1>#TITLE#</h1>'||unistr('\000a')||
'  #NEXT#'||unistr('\000a')||
'  #BODY#'||unistr('\000a')||
'</div> <!-- /header -->'
 ,p_page_plug_template_name => 'Header Toolbar (Fullscreen)'
 ,p_theme_id => 50
 ,p_theme_class_id => 21
 ,p_default_label_alignment => 'RIGHT'
 ,p_default_field_alignment => 'LEFT'
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/region/plain_no_title
prompt  ......region template 2225601475528836
 
begin
 
wwv_flow_api.create_plug_template (
  p_id => 2225601475528836 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_layout => 'TABLE'
 ,p_template => 
'<div id="#REGION_STATIC_ID#">'||unistr('\000a')||
'#BODY#'||unistr('\000a')||
'#SUB_REGIONS#'||unistr('\000a')||
'#CLOSE#'||unistr('\000a')||
'#DELETE#'||unistr('\000a')||
'#CREATE#'||unistr('\000a')||
'#EDIT#'||unistr('\000a')||
'#CHANGE#'||unistr('\000a')||
'</div>'
 ,p_page_plug_template_name => 'Plain (No Title)'
 ,p_theme_id => 50
 ,p_theme_class_id => 7
 ,p_default_label_alignment => 'RIGHT'
 ,p_default_field_alignment => 'LEFT'
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/region/region_with_title_bar
prompt  ......region template 2225709335528836
 
begin
 
wwv_flow_api.create_plug_template (
  p_id => 2225709335528836 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_layout => 'TABLE'
 ,p_template => 
'<div id="#REGION_STATIC_ID#" class="ui-collapsible #REGION_CSS_CLASSES#" data-theme="b" #REGION_ATTRIBUTES#>'||unistr('\000a')||
'  <h3 class="ui-collapsible-heading ui-btn ui-corner-top ui-btn-up-a">'||unistr('\000a')||
'    <span class="ui-btn-inner ui-corner-top ui-corner-bottom">'||unistr('\000a')||
'      <span lass="ui-btn-text">#TITLE#</span>'||unistr('\000a')||
'    </span>'||unistr('\000a')||
'  </h3>'||unistr('\000a')||
'  <div class="ui-collapsible-content ui-body-c ui-corner-bottom">'||unistr('\000a')||
'    #BODY#'||unistr('\000a')||
'    #SUB_REGIO'||
'NS#'||unistr('\000a')||
'    #EDIT#'||unistr('\000a')||
'  </div>'||unistr('\000a')||
'</div>'
 ,p_page_plug_template_name => 'Region (With Title Bar)'
 ,p_theme_id => 50
 ,p_theme_class_id => 8
 ,p_default_label_alignment => 'RIGHT'
 ,p_default_field_alignment => 'LEFT'
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

--application/shared_components/user_interface/templates/region/region_with_title
prompt  ......region template 2225824916528837
 
begin
 
wwv_flow_api.create_plug_template (
  p_id => 2225824916528837 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_layout => 'TABLE'
 ,p_template => 
'<div id="#REGION_STATIC_ID#" class="#REGION_CSS_CLASSES#" data-theme="b" #REGION_ATTRIBUTES#>'||unistr('\000a')||
'<h3>#TITLE#</h3>'||unistr('\000a')||
'#BODY#'||unistr('\000a')||
'#SUB_REGIONS#'||unistr('\000a')||
'#EDIT#'||unistr('\000a')||
'</div>'
 ,p_page_plug_template_name => 'Region (With Title)'
 ,p_theme_id => 50
 ,p_theme_class_id => 7
 ,p_default_label_alignment => 'RIGHT'
 ,p_default_field_alignment => 'LEFT'
 ,p_translate_this_template => 'N'
  );
null;
 
end;
/

prompt  ...List Templates
--
--application/shared_components/user_interface/templates/list/button_control_group
prompt  ......list template 2226030223528851
 
begin
 
declare
  t varchar2(32767) := null;
  t2 varchar2(32767) := null;
  t3 varchar2(32767) := null;
  t4 varchar2(32767) := null;
  t5 varchar2(32767) := null;
  t6 varchar2(32767) := null;
  t7 varchar2(32767) := null;
  t8 varchar2(32767) := null;
  l_clob clob;
  l_clob2 clob;
  l_clob3 clob;
  l_clob4 clob;
  l_clob5 clob;
  l_clob6 clob;
  l_clob7 clob;
  l_clob8 clob;
  l_length number := 1;
begin
t:=t||'<a href="#LINK#" rel="external" data-role="button" data-icon="#A01#">#TEXT_ESC_SC#</a>';

t2:=t2||'<a href="#LINK#" rel="external" data-role="button" data-icon="#A01#">#TEXT_ESC_SC#</a>';

t3 := null;
t4 := null;
t5 := null;
t6 := null;
t7 := null;
t8 := null;
wwv_flow_api.create_list_template (
  p_id=>2226030223528851 + wwv_flow_api.g_id_offset,
  p_flow_id=>wwv_flow.g_flow_id,
  p_list_template_current=>t,
  p_list_template_noncurrent=> t2,
  p_list_template_name=>'Button Control Group',
  p_theme_id  => 50,
  p_theme_class_id => 9,
  p_list_template_before_rows=>'<div id="navgroup">'||unistr('\000a')||
'   <div data-role="controlgroup" data-type="horizontal">',
  p_list_template_after_rows=>'</div></div>',
  p_translate_this_template => 'N',
  p_list_template_comment=>'');
end;
null;
 
end;
/

--application/shared_components/user_interface/templates/list/list_view
prompt  ......list template 2226104105528856
 
begin
 
declare
  t varchar2(32767) := null;
  t2 varchar2(32767) := null;
  t3 varchar2(32767) := null;
  t4 varchar2(32767) := null;
  t5 varchar2(32767) := null;
  t6 varchar2(32767) := null;
  t7 varchar2(32767) := null;
  t8 varchar2(32767) := null;
  l_clob clob;
  l_clob2 clob;
  l_clob3 clob;
  l_clob4 clob;
  l_clob5 clob;
  l_clob6 clob;
  l_clob7 clob;
  l_clob8 clob;
  l_length number := 1;
begin
t:=t||'<li data-theme="b"><a href="#LINK#">#TEXT_ESC_SC#</a></li>';

t2:=t2||'<li><a href="#LINK#">#TEXT_ESC_SC#</a></li>';

t3:=t3||'<li data-theme="b"><a href="#LINK#">#TEXT_ESC_SC#</a></li>';

t4:=t4||'<li><a href="#LINK#">#TEXT_ESC_SC#</a></li>';

t5:=t5||'<li data-theme="b">'||unistr('\000a')||
'<a href="#LINK#">#TEXT_ESC_SC#</a>'||unistr('\000a')||
'#SUB_LISTS#'||unistr('\000a')||
'</li>';

t6:=t6||'<li>'||unistr('\000a')||
'<a href="#LINK#">#TEXT_ESC_SC#</a>'||unistr('\000a')||
'#SUB_LISTS#'||unistr('\000a')||
'</li>';

t7:=t7||'<li data-theme="b">'||unistr('\000a')||
'<a href="#LINK#">#TEXT_ESC_SC#</a>'||unistr('\000a')||
'#SUB_LISTS#'||unistr('\000a')||
'</li>';

t8:=t8||'<li>'||unistr('\000a')||
'<a href="#LINK#">#TEXT_ESC_SC#</a>'||unistr('\000a')||
'#SUB_LISTS#'||unistr('\000a')||
'</li>';

wwv_flow_api.create_list_template (
  p_id=>2226104105528856 + wwv_flow_api.g_id_offset,
  p_flow_id=>wwv_flow.g_flow_id,
  p_list_template_current=>t,
  p_list_template_noncurrent=> t2,
  p_list_template_name=>'List View',
  p_theme_id  => 50,
  p_theme_class_id => 9,
  p_list_template_before_rows=>'<ul data-role="listview">',
  p_list_template_after_rows=>'</ul>',
  p_before_sub_list=>'<ul>',
  p_after_sub_list=>'</ul>',
  p_sub_list_item_current=> t3,
  p_sub_list_item_noncurrent=> t4,
  p_item_templ_curr_w_child=> t5,
  p_item_templ_noncurr_w_child=> t6,
  p_sub_templ_curr_w_child=> t7,
  p_sub_templ_noncurr_w_child=> t8,
  p_translate_this_template => 'N',
  p_list_template_comment=>'');
end;
null;
 
end;
/

--application/shared_components/user_interface/templates/list/list_view_inset
prompt  ......list template 2226200778528871
 
begin
 
declare
  t varchar2(32767) := null;
  t2 varchar2(32767) := null;
  t3 varchar2(32767) := null;
  t4 varchar2(32767) := null;
  t5 varchar2(32767) := null;
  t6 varchar2(32767) := null;
  t7 varchar2(32767) := null;
  t8 varchar2(32767) := null;
  l_clob clob;
  l_clob2 clob;
  l_clob3 clob;
  l_clob4 clob;
  l_clob5 clob;
  l_clob6 clob;
  l_clob7 clob;
  l_clob8 clob;
  l_length number := 1;
begin
t:=t||'<li data-theme="b"><a href="#LINK#">#TEXT_ESC_SC#</a></li>';

t2:=t2||'<li><a href="#LINK#">#TEXT_ESC_SC#</a></li>';

t3:=t3||'<li data-theme="b"><a href="#LINK#">#TEXT_ESC_SC#</a></li>';

t4:=t4||'<li><a href="#LINK#">#TEXT_ESC_SC#</a></li>';

t5:=t5||'<li data-theme="b">'||unistr('\000a')||
'<a href="#LINK#">#TEXT_ESC_SC#</a>'||unistr('\000a')||
'#SUB_LISTS#'||unistr('\000a')||
'</li>';

t6:=t6||'<li>'||unistr('\000a')||
'<a href="#LINK#">#TEXT_ESC_SC#</a>'||unistr('\000a')||
'#SUB_LISTS#'||unistr('\000a')||
'</li>';

t7:=t7||'<li data-theme="b">'||unistr('\000a')||
'<a href="#LINK#">#TEXT_ESC_SC#</a>'||unistr('\000a')||
'#SUB_LISTS#'||unistr('\000a')||
'</li>';

t8:=t8||'<li>'||unistr('\000a')||
'<a href="#LINK#">#TEXT_ESC_SC#</a>'||unistr('\000a')||
'#SUB_LISTS#'||unistr('\000a')||
'</li>';

wwv_flow_api.create_list_template (
  p_id=>2226200778528871 + wwv_flow_api.g_id_offset,
  p_flow_id=>wwv_flow.g_flow_id,
  p_list_template_current=>t,
  p_list_template_noncurrent=> t2,
  p_list_template_name=>'List View (Inset)',
  p_theme_id  => 50,
  p_theme_class_id => 10,
  p_list_template_before_rows=>'<ul data-role="listview" data-inset="true">',
  p_list_template_after_rows=>'</ul>',
  p_before_sub_list=>'<ul data-inset="true">',
  p_after_sub_list=>'</ul>',
  p_sub_list_item_current=> t3,
  p_sub_list_item_noncurrent=> t4,
  p_item_templ_curr_w_child=> t5,
  p_item_templ_noncurr_w_child=> t6,
  p_sub_templ_curr_w_child=> t7,
  p_sub_templ_noncurr_w_child=> t8,
  p_translate_this_template => 'N',
  p_list_template_comment=>'');
end;
null;
 
end;
/

--application/shared_components/user_interface/templates/list/navigation_bar
prompt  ......list template 2226308453528873
 
begin
 
declare
  t varchar2(32767) := null;
  t2 varchar2(32767) := null;
  t3 varchar2(32767) := null;
  t4 varchar2(32767) := null;
  t5 varchar2(32767) := null;
  t6 varchar2(32767) := null;
  t7 varchar2(32767) := null;
  t8 varchar2(32767) := null;
  l_clob clob;
  l_clob2 clob;
  l_clob3 clob;
  l_clob4 clob;
  l_clob5 clob;
  l_clob6 clob;
  l_clob7 clob;
  l_clob8 clob;
  l_length number := 1;
begin
t:=t||'<li><a href="#LINK#" class="ui-btn-active" data-transition="flow" data-icon="#IMAGE#">#TEXT_ESC_SC#</a></li>';

t2:=t2||'<li><a href="#LINK#" data-transition="flow" data-icon="#IMAGE#">#TEXT_ESC_SC#</a></li>';

t3 := null;
t4 := null;
t5 := null;
t6 := null;
t7 := null;
t8 := null;
wwv_flow_api.create_list_template (
  p_id=>2226308453528873 + wwv_flow_api.g_id_offset,
  p_flow_id=>wwv_flow.g_flow_id,
  p_list_template_current=>t,
  p_list_template_noncurrent=> t2,
  p_list_template_name=>'Navigation Bar',
  p_theme_id  => 50,
  p_theme_class_id => 11,
  p_list_template_before_rows=>'<div data-role="navbar">'||unistr('\000a')||
'  <ul>'||unistr('\000a')||
'',
  p_list_template_after_rows=>'  </ul>'||unistr('\000a')||
'</div><!-- /navbar -->',
  p_translate_this_template => 'N',
  p_list_template_comment=>'');
end;
null;
 
end;
/

prompt  ...report templates
--
--application/shared_components/user_interface/templates/report/standard_report
prompt  ......report template 2225901133528838
 
begin
 
declare
  c1 varchar2(32767) := null;
  c2 varchar2(32767) := null;
  c3 varchar2(32767) := null;
  c4 varchar2(32767) := null;
begin
c1:=c1||'<div class="list-view-cell" style="width:#COLUMN_WIDTH_VAL#%">#COLUMN_VALUE#</div>';

c2 := null;
c3 := null;
c4 := null;
wwv_flow_api.create_row_template (
  p_id=> 2225901133528838 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_row_template_name=> 'Standard Report',
  p_row_template1=> c1,
  p_row_template_condition1=> '',
  p_row_template2=> c2,
  p_row_template_condition2=> '',
  p_row_template3=> c3,
  p_row_template_condition3=> '',
  p_row_template4=> c4,
  p_row_template_condition4=> '',
  p_row_template_before_rows=>'<div #REPORT_ATTRIBUTES# id="report_#REGION_STATIC_ID#">'||unistr('\000a')||
'<ul data-role="listview">',
  p_row_template_after_rows =>'</ul>'||unistr('\000a')||
'</div>',
  p_row_template_table_attr =>'',
  p_row_template_type =>'GENERIC_COLUMNS',
  p_before_column_heading=>'<li data-role="list-divider">',
  p_column_heading_template=>'<div class="list-view-cell" id="#COLUMN_HEADER_NAME#" style="width:#COLUMN_WIDTH_VAL#%">#COLUMN_HEADER#</div>',
  p_after_column_heading=>'</li>',
  p_row_template_display_cond1=>'0',
  p_row_template_display_cond2=>'0',
  p_row_template_display_cond3=>'0',
  p_row_template_display_cond4=>'0',
  p_next_page_template=>'<a href="#LINK#" data-role="button" data-icon="arrow-r" data-iconpos="right" data-transition="slide">#PAGINATION_NEXT#</a>',
  p_previous_page_template=>'<a href="#LINK#" data-role="button" data-icon="arrow-l" data-iconpos="left" data-transition="slide" data-direction="reverse">#PAGINATION_PREVIOUS#</a>',
  p_next_set_template=>'<a href="#LINK#" data-role="button" data-icon="arrow-r" data-iconpos="right" data-transition="slide">#PAGINATION_NEXT_SET#</a>',
  p_previous_set_template=>'<a href="#LINK#" data-role="button" data-icon="arrow-l" data-iconpos="left" data-transition="slide" data-direction="reverse">#PAGINATION_PREVIOUS_SET#</a>',
  p_row_style_checked=>'#ccc',
  p_theme_id  => 50,
  p_theme_class_id => 4,
  p_translate_this_template => 'N',
  p_row_template_comment=> '');
end;
null;
 
end;
/

 
begin
 
begin
wwv_flow_api.create_row_template_patch (
  p_id => 2225901133528838 + wwv_flow_api.g_id_offset,
  p_row_template_before_first =>'<li>',
  p_row_template_after_last =>'</li>');
exception when others then null;
end;
null;
 
end;
/

prompt  ...label templates
--
--application/shared_components/user_interface/templates/label/no_label_for_screenreaders
prompt  ......label template 2226411668528877
 
begin
 
begin
wwv_flow_api.create_field_template (
  p_id=> 2226411668528877 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_template_name=>'No Label (For Screenreaders)',
  p_template_body1=>'<label for="#CURRENT_ITEM_NAME#">',
  p_template_body2=>'</label>',
  p_before_item=>'<div data-role="fieldcontain" class="ui-hide-label" id="#CURRENT_ITEM_CONTAINER_ID#">',
  p_after_item=>'</div>',
  p_on_error_before_label=>' ',
  p_on_error_after_label=>' ',
  p_theme_id  => 50,
  p_theme_class_id => 13,
  p_translate_this_template=> 'N',
  p_template_comment=> '');
end;
null;
 
end;
/

--application/shared_components/user_interface/templates/label/optional
prompt  ......label template 2226531044528882
 
begin
 
begin
wwv_flow_api.create_field_template (
  p_id=> 2226531044528882 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_template_name=>'Optional',
  p_template_body1=>'<label for="#CURRENT_ITEM_NAME#">',
  p_template_body2=>'</label>',
  p_before_item=>'<div data-role="fieldcontain" id="#CURRENT_ITEM_CONTAINER_ID#">',
  p_after_item=>'</div>',
  p_on_error_before_label=>' ',
  p_on_error_after_label=>' ',
  p_theme_id  => 50,
  p_theme_class_id => 3,
  p_translate_this_template=> 'N',
  p_template_comment=> '');
end;
null;
 
end;
/

--application/shared_components/user_interface/templates/label/required
prompt  ......label template 2226614227528882
 
begin
 
begin
wwv_flow_api.create_field_template (
  p_id=> 2226614227528882 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_template_name=>'Required',
  p_template_body1=>'<label for="#CURRENT_ITEM_NAME#"><img src="#IMAGE_PREFIX#themes/theme_21/images/required.png" alt="#VALUE_REQUIRED#">',
  p_template_body2=>'</label>',
  p_before_item=>'<div data-role="fieldcontain" id="#CURRENT_ITEM_CONTAINER_ID#">',
  p_after_item=>'</div>',
  p_on_error_before_label=>' ',
  p_on_error_after_label=>' ',
  p_theme_id  => 50,
  p_theme_class_id => 4,
  p_translate_this_template=> 'N',
  p_template_comment=> '');
end;
null;
 
end;
/

prompt  ...breadcrumb templates
--
--application/shared_components/user_interface/templates/breadcrumb/breadcrumb
prompt  ......template 2227032197528894
 
begin
 
begin
wwv_flow_api.create_menu_template (
  p_id=> 2227032197528894 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_name=>'Breadcrumb',
  p_before_first=>'',
  p_current_page_option=>'#NAME#',
  p_non_current_page_option=>'<a href="#LINK#">#NAME#</a>',
  p_menu_link_attributes=>'',
  p_between_levels=>'&nbsp;&gt;&nbsp;',
  p_after_last=>'',
  p_max_levels=>12,
  p_start_with_node=>'PARENT_TO_LEAF',
  p_theme_id  => 50,
  p_theme_class_id => 1,
  p_translate_this_template => 'N',
  p_template_comments=>'');
end;
null;
 
end;
/

--application/shared_components/user_interface/templates/popuplov
prompt  ...popup list of values templates
--
prompt  ......template 2227221043528908
 
begin
 
begin
wwv_flow_api.create_popup_lov_template (
  p_id=> 2227221043528908 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_popup_icon=>'#IMAGE_PREFIX#list.gif',
  p_popup_icon_attr=>'width=13 height=13 alt="#LIST_OF_VALUES#" title="#LIST_OF_VALUES#"',
  p_popup_icon2=>'',
  p_popup_icon_attr2=>'',
  p_page_name=>'winlov',
  p_page_title=>'Search Dialog',
  p_page_html_head=>'<link rel=stylesheet href=#IMAGE_PREFIX#platform2.css type=text/css>'||unistr('\000a')||
'#THEME_CSS#',
  p_page_body_attr=>'bgcolor=white OnLoad=first_field()',
  p_before_field_text=>' ',
  p_page_heading_text=>'<link rel=stylesheet href=#IMAGE_PREFIX#platform2.css type=text/css><style>a:link { color:#336699; text-decoration:none; padding:2px;} a:visited { color:#336699; text-decoration:none;} a:hover { color:red; text-decoration:underline;} body { font-family:arial; background-color:#ffffff;} </style>',
  p_page_footer_text =>'</center></td></tr></table>',
  p_filter_width     =>'20',
  p_filter_max_width =>'100',
  p_filter_text_attr =>'',
  p_find_button_text =>'Search',
  p_find_button_image=>'',
  p_find_button_attr =>'',
  p_close_button_text=>'Close',
  p_close_button_image=>'',
  p_close_button_attr=>'',
  p_next_button_text =>'Next',
  p_next_button_image=>'',
  p_next_button_attr =>'',
  p_prev_button_text =>'Previous',
  p_prev_button_image=>'',
  p_prev_button_attr =>'',
  p_after_field_text=>'</div><br />',
  p_scrollbars=>'1',
  p_resizable=>'1',
  p_width =>'400',
  p_height=>'450',
  p_result_row_x_of_y=>'Row(s) #FIRST_ROW# - #LAST_ROW#',
  p_result_rows_per_pg=>10,
  p_before_result_set=>'',
  p_theme_id  => 50,
  p_theme_class_id => 1,
  p_translate_this_template => 'N',
  p_after_result_set   =>'');
end;
null;
 
end;
/

prompt  ...calendar templates
--
--application/shared_components/user_interface/templates/calendar/basic_calendar
prompt  ......template 2227131509528901
 
begin
 
begin
wwv_flow_api.create_calendar_template(
  p_id=> 2227131509528901 + wwv_flow_api.g_id_offset,
  p_flow_id=> wwv_flow.g_flow_id,
  p_cal_template_name=>'Basic Calendar',
  p_translate_this_template=> 'N',
  p_day_of_week_format=> '<th scope="col" class="m-DayOfWeek">#IDY#</th>',
  p_month_title_format=> '<table summary="#CALENDAR_TITLE# #IMONTH# #YYYY#" class="m-Calendar" id="calendar_month_#REGION_STATIC_ID#" data-enhance=false>'||unistr('\000a')||
'<tr><th scope="colgroup" class="m-MonthTitle" colspan="7" >#IMONTH# #YYYY#</th></tr>'||unistr('\000a')||
'',
  p_month_open_format=> '',
  p_month_close_format=> '<tr><td colspan="7">'||unistr('\000a')||
'<div id="calendar_day_details_#REGION_STATIC_ID#"></div>'||unistr('\000a')||
'</td>'||unistr('\000a')||
'</tr>'||unistr('\000a')||
'</table>'||unistr('\000a')||
'<script>'||unistr('\000a')||
'apex.jQuery( "##PAGE_STATIC_ID#" ).on( "pageinit", function() {'||unistr('\000a')||
''||unistr('\000a')||
'    // Initialize calendar data as list view as soon as that section has been refreshed'||unistr('\000a')||
'    // Use Swipe Left and Right to go to the next or previous month'||unistr('\000a')||
'    apex.jQuery( "##REGION_STATIC_ID#", apex.gPageContext$ )'||unistr('\000a')||
'        .on( "apexafterrefresh", function() {'||unistr('\000a')||
'            apex.jQuery( "#calendar_month_#REGION_STATIC_ID#", apex.gPageContext$ )'||unistr('\000a')||
'                .find( "[data-role=''listview'']")'||unistr('\000a')||
'                .listview(); })'||unistr('\000a')||
'        .on( "swipeleft", function() {'||unistr('\000a')||
'            apex.widget.calendar.ajax_calendar(''S'', ''next''); } )'||unistr('\000a')||
'        .on( "swiperight", function() {'||unistr('\000a')||
'            apex.widget.calendar.ajax_calendar(''S'', ''previous''); } );'||unistr('\000a')||
''||unistr('\000a')||
'    // Load calendar data of date if it''s tapped'||unistr('\000a')||
'    apex.jQuery( "##REGION_STATIC_ID#", apex.gPageContext$ ).on( "tap", "#calendar_month_#REGION_STATIC_ID# td[class*=m-]", function () {'||unistr('\000a')||
'        var lDate     = apex.jQuery( this ).data( "date" ),'||unistr('\000a')||
'            lDetails$ = apex.jQuery( "#calendar_day_details_#REGION_STATIC_ID#", apex.gPageContext$ );'||unistr('\000a')||
'        apex.jQuery(".apex-calendar-today-has-data")'||unistr('\000a')||
'            .addClass(''apex-calendar-has-data'')'||unistr('\000a')||
'            .removeClass("apex-calendar-today-has-data");'||unistr('\000a')||
'        apex.jQuery( ".m-Today" )'||unistr('\000a')||
'            .addClass("m-Day")'||unistr('\000a')||
'            .removeClass( "m-Today" );'||unistr('\000a')||
'        apex.jQuery( this ).addClass( "m-Today" );'||unistr('\000a')||
''||unistr('\000a')||
'        apex.widget.calendar.getDayData( "#REGION_STATIC_ID#", lDate, {'||unistr('\000a')||
'            clear: function() {'||unistr('\000a')||
'                lDetails$.empty();'||unistr('\000a')||
'            },'||unistr('\000a')||
'            success: function( pData ) {'||unistr('\000a')||
'                lDetails$.html( pData );'||unistr('\000a')||
'            }'||unistr('\000a')||
'        });'||unistr('\000a')||
'    });'||unistr('\000a')||
''||unistr('\000a')||
'    // New calendar entries can be added with tab and hold'||unistr('\000a')||
'    apex.jQuery( "##REGION_STATIC_ID#", apex.gPageContext$ ).on( "taphold", "#calendar_month_#REGION_STATIC_ID# td[class*=m-]", function (e) {'||unistr('\000a')||
'       apex.widget.calendar.ajaxAddData(e);'||unistr('\000a')||
'    });'||unistr('\000a')||
''||unistr('\000a')||
'    // Get the data of the current day as soon as the page gets displayed'||unistr('\000a')||
'    apex.jQuery( "##PAGE_STATIC_ID#" ).on( "pageshow", function() {'||unistr('\000a')||
'        var lDetails$ = apex.jQuery( "#calendar_day_details_#REGION_STATIC_ID#", apex.gPageContext$ );'||unistr('\000a')||
'        apex.jQuery( ".m-Today", apex.gPageContext$ )'||unistr('\000a')||
'            .addClass( "m-Day" )'||unistr('\000a')||
'            .removeClass( "m-Today" );'||unistr('\000a')||
'        apex.jQuery( "#calendar_month_#REGION_STATIC_ID#", apex.gPageContext$ )'||unistr('\000a')||
'            .find( "td[data-date=#CURRENT_DATE#]" )'||unistr('\000a')||
'            .addClass( "m-Today" );'||unistr('\000a')||
'        apex.widget.calendar.getDayData( "#REGION_STATIC_ID#", "#CURRENT_DATE#", {'||unistr('\000a')||
'            clear: function() {'||unistr('\000a')||
'                lDetails$.empty();'||unistr('\000a')||
'            },'||unistr('\000a')||
'            success: function( pData ) {'||unistr('\000a')||
'                lDetails$.html( pData );'||unistr('\000a')||
'            }'||unistr('\000a')||
'        });'||unistr('\000a')||
'    });'||unistr('\000a')||
''||unistr('\000a')||
'});'||unistr('\000a')||
'</script>',
  p_day_title_format=> '<div class="content-primary" class="m-DayTitle">#DD#</div>',
  p_day_open_format=> '<td class="m-Day #HAS_DATA#" data-date="#CANONICAL_DATE#">#TITLE_FORMAT#',
  p_day_close_format=> '</td>',
  p_today_open_format=> '<td class="m-Today #HAS_DATA#" data-date="#CANONICAL_DATE#">#TITLE_FORMAT#',
  p_weekend_title_format=> '<div class="content-primary" class="m-WeekendDayTitle">#DD#</div>'||unistr('\000a')||
'',
  p_weekend_open_format => '<td class="m-WeekendDay #HAS_DATA#"  data-date="#CANONICAL_DATE#">#TITLE_FORMAT#',
  p_weekend_close_format => '</td>',
  p_nonday_title_format => '<div class="content-primary" class="m-NonDayTitle">#DD#</div>',
  p_nonday_open_format => '<td class="m-NonDay"  data-date="#CANONICAL_DATE#">',
  p_nonday_close_format => '</td>',
  p_week_title_format => '',
  p_week_open_format => '<tr>',
  p_week_close_format => '</tr> ',
  p_daily_title_format => '',
  p_daily_open_format => '',
  p_daily_close_format => '',
  p_weekly_title_format => '<table summary="#CALENDAR_TITLE# #START_DL# - #END_DL#" class="m-WeekCalendar">'||unistr('\000a')||
'	<tr>'||unistr('\000a')||
'        <th scope="colgroup" class="m-monthTitle" colspan="7" >#WTITLE#</th>'||unistr('\000a')||
'	</tr>',
  p_weekly_day_of_week_format => '<th scope="col" class="m-DayOfWeek" >#IDY# #MM#/#DD#</th>'||unistr('\000a')||
'',
  p_weekly_month_open_format => '',
  p_weekly_month_close_format => '</table>'||unistr('\000a')||
'<script>'||unistr('\000a')||
'    // register a delegated event on the table listening for all taphold in TD''s'||unistr('\000a')||
'    apex.jQuery( "##REGION_STATIC_ID#" ).on( "taphold", "td[class^=m-]", function (e) {'||unistr('\000a')||
'        apex.widget.calendar.ajaxAddData(e);'||unistr('\000a')||
'    });'||unistr('\000a')||
'</script>',
  p_weekly_day_title_format => '',
  p_weekly_day_open_format => '<td class="m-Day">',
  p_weekly_day_close_format => '</td>',
  p_weekly_today_open_format => '<td class="m-Today">',
  p_weekly_weekend_title_format => '',
  p_weekly_weekend_open_format => '<td class="m-NonDay">',
  p_weekly_weekend_close_format => '</td>',
  p_weekly_time_open_format => '<th scope="row" class="m-hour">',
  p_weekly_time_close_format => '</th>',
  p_weekly_time_title_format => '#TIME#',
  p_weekly_hour_open_format => '<tr rowspan="2">',
  p_weekly_hour_close_format => '</tr>',
  p_daily_day_of_week_format => '<th scope="col" class="m-DayOfWeek">#IDAY# #DD# #IMONTH# #YYYY#</th>',
  p_daily_month_title_format => '<table summary="#CALENDAR_TITLE# #START_DL#" class="m-DayCalendar"><tr>'||unistr('\000a')||
'',
  p_daily_month_open_format => '',
  p_daily_month_close_format => '</table>'||unistr('\000a')||
'<script>'||unistr('\000a')||
'    // register a delegated event on the table listening for all taphold in TD''s'||unistr('\000a')||
'    apex.jQuery( "##REGION_STATIC_ID#" ).on( "taphold", "td[class^=m-]", function (e) {'||unistr('\000a')||
'        apex.widget.calendar.ajaxAddData(e);'||unistr('\000a')||
'    });'||unistr('\000a')||
'</script>',
  p_daily_day_title_format => '',
  p_daily_day_open_format => '<td class="m-Day">',
  p_daily_day_close_format => '</td>',
  p_daily_today_open_format => '<td class="m-ToDay">',
  p_daily_time_open_format => '<th scope="row" class="m-hour">',
  p_daily_time_close_format => '</th>',
  p_daily_time_title_format => '#TIME#',
  p_daily_hour_open_format => '<tr rowspan="2" >',
  p_daily_hour_close_format => '</tr>',
  p_cust_month_title_format => '',
  p_cust_day_of_week_format => '',
  p_cust_month_open_format => '',
  p_cust_month_close_format => '',
  p_cust_week_title_format => '',
  p_cust_week_open_format => '',
  p_cust_week_close_format => '',
  p_cust_day_title_format => '',
  p_cust_day_open_format => '',
  p_cust_day_close_format => '',
  p_cust_today_open_format => '',
  p_cust_daily_title_format => '',
  p_cust_daily_open_format => '',
  p_cust_daily_close_format => '',
  p_cust_nonday_title_format => '',
  p_cust_nonday_open_format => '',
  p_cust_nonday_close_format => '',
  p_cust_weekend_title_format => '',
  p_cust_weekend_open_format => '',
  p_cust_weekend_close_format => '',
  p_cust_hour_open_format => '',
  p_cust_hour_close_format => '',
  p_cust_time_title_format => '',
  p_cust_time_open_format => '',
  p_cust_time_close_format => '',
  p_cust_wk_month_title_format => '',
  p_cust_wk_day_of_week_format => '',
  p_cust_wk_month_open_format => '',
  p_cust_wk_month_close_format => '',
  p_cust_wk_week_title_format => '',
  p_cust_wk_week_open_format => '',
  p_cust_wk_week_close_format => '',
  p_cust_wk_day_title_format => '',
  p_cust_wk_day_open_format => '',
  p_cust_wk_day_close_format => '',
  p_cust_wk_today_open_format => '',
  p_cust_wk_weekend_title_format => '',
  p_cust_wk_weekend_open_format => '',
  p_cust_wk_weekend_close_format => '',
  p_cust_month_day_height_pix => '',
  p_cust_month_day_height_per => '',
  p_cust_week_day_width_pix => '',
  p_cust_week_day_width_per => '',
  p_agenda_format => '<ul data-role="listview">'||unistr('\000a')||
'#DAYS#'||unistr('\000a')||
'</ul>'||unistr('\000a')||
'',
  p_agenda_past_day_format => '<li data-role="list-divider" data-theme="d">#IDAY# #IMONTH# #DD# #YYYY#</li>',
  p_agenda_today_day_format => '<li data-role="list-divider" data-theme="b">#IDAY# #IMONTH# #DD# #YYYY#</li>',
  p_agenda_future_day_format => '<li  data-role="list-divider" >#IDAY# #IMONTH# #DD# #YYYY#</li>',
  p_agenda_past_entry_format => '<li >#DATA#<p class="ui-li-aside">#TIME#</p></li>',
  p_agenda_today_entry_format => '<li >#DATA#<p class="ui-li-aside">#TIME#</p></li>',
  p_agenda_future_entry_format => '<li >#DATA#<p class="ui-li-aside">#TIME#</p></li>',
  p_month_data_format => '<ul id="listview_#REGION_STATIC_ID#" data-role="listview" data-inset="true" data-theme="c">#DAYS#</ul>'||unistr('\000a')||
'',
  p_month_data_entry_format => '<li align="left" >#LINK##DATA#<p class="ui-li-aside">#TIME#</p></li>'||unistr('\000a')||
' ',
  p_theme_id  => 50,
  p_theme_class_id => 1,
  p_reference_id=> null);
end;
null;
 
end;
/

prompt  ...application themes
--
--application/shared_components/user_interface/themes/jquery_mobile_smartphone
prompt  ......theme 2227305494528934
begin
wwv_flow_api.create_theme (
  p_id =>2227305494528934 + wwv_flow_api.g_id_offset,
  p_flow_id =>wwv_flow.g_flow_id,
  p_theme_id  => 50,
  p_theme_name=>'jQuery Mobile Smartphone',
  p_ui_type_name=>'JQM_SMARTPHONE',
  p_is_locked=>false,
  p_default_page_template=>2224121284528793 + wwv_flow_api.g_id_offset,
  p_error_template=>2224121284528793 + wwv_flow_api.g_id_offset,
  p_printer_friendly_template=>2224121284528793 + wwv_flow_api.g_id_offset,
  p_breadcrumb_display_point=>'',
  p_sidebar_display_point=>'',
  p_login_template=>null + wwv_flow_api.g_id_offset,
  p_default_button_template=>2226903194528893 + wwv_flow_api.g_id_offset,
  p_default_region_template=>2225601475528836 + wwv_flow_api.g_id_offset,
  p_default_chart_template =>2225601475528836 + wwv_flow_api.g_id_offset,
  p_default_form_template  =>2225601475528836 + wwv_flow_api.g_id_offset,
  p_default_reportr_template   =>2225824916528837 + wwv_flow_api.g_id_offset,
  p_default_tabform_template=>2225601475528836 + wwv_flow_api.g_id_offset,
  p_default_wizard_template=>2225601475528836 + wwv_flow_api.g_id_offset,
  p_default_menur_template=>null + wwv_flow_api.g_id_offset,
  p_default_listr_template=>null + wwv_flow_api.g_id_offset,
  p_default_irr_template=>2225601475528836 + wwv_flow_api.g_id_offset,
  p_default_report_template   =>2225901133528838 + wwv_flow_api.g_id_offset,
  p_default_label_template=>2226531044528882 + wwv_flow_api.g_id_offset,
  p_default_menu_template=>null + wwv_flow_api.g_id_offset,
  p_default_calendar_template=>2227131509528901 + wwv_flow_api.g_id_offset,
  p_default_list_template=>2226200778528871 + wwv_flow_api.g_id_offset,
  p_default_option_label=>2226531044528882 + wwv_flow_api.g_id_offset,
  p_default_header_template=>2225409203528836 + wwv_flow_api.g_id_offset,
  p_default_footer_template=>2225211651528835 + wwv_flow_api.g_id_offset,
  p_default_page_transition=>'SLIDE',
  p_default_popup_transition=>'POP',
  p_default_required_label=>2226614227528882 + wwv_flow_api.g_id_offset);
end;
/
 
prompt  ...theme styles
--
 
begin
 
null;
 
end;
/

prompt  ...theme display points
--
 
begin
 
null;
 
end;
/

prompt  ...build options
--
 
begin
 
null;
 
end;
/

--application/shared_components/globalization/language
prompt  ...Language Maps for Application 101
--
 
begin
 
null;
 
end;
/

--application/shared_components/globalization/messages
prompt  ...text messages
--
--application/shared_components/globalization/dyntranslations
prompt  ...dynamic translations
--
prompt  ...Shortcuts
--
prompt  ...web services (9iR2 or better)
--
prompt  ...shared queries
--
prompt  ...report layouts
--
prompt  ...authentication schemes
--
--application/shared_components/security/authentication/application_express_authentication
prompt  ......authentication 2227607043528968
 
begin
 
wwv_flow_api.create_authentication (
  p_id => 2227607043528968 + wwv_flow_api.g_id_offset
 ,p_flow_id => wwv_flow.g_flow_id
 ,p_name => 'Application Express Authentication'
 ,p_scheme_type => 'NATIVE_APEX_ACCOUNTS'
 ,p_invalid_session_type => 'LOGIN'
 ,p_use_secure_cookie_yn => 'N'
  );
null;
 
end;
/

prompt  ...ui types
--
 
begin
 
null;
 
end;
/

prompt  ...plugins
--
prompt  ...data loading
--
prompt  ...post import process
 
begin
 
wwv_flow_api.post_import_process(p_flow_id => wwv_flow.g_flow_id);
null;
 
end;
/

--application/end_environment
commit;
commit;
begin
execute immediate 'begin sys.dbms_session.set_nls( param => ''NLS_NUMERIC_CHARACTERS'', value => '''''''' || replace(wwv_flow_api.g_nls_numeric_chars,'''''''','''''''''''') || ''''''''); end;';
end;
/
set verify on
set feedback on
set define on
prompt  ...done


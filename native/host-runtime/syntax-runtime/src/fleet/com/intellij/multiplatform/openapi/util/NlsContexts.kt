// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package fleet.com.intellij.multiplatform.openapi.util

import kotlin.annotation.AnnotationTarget.*

/**
 * See the [IntelliJ Platform UI Guidelines](https://plugins.jetbrains.com/docs/intellij/capitalization.html).
 */
class NlsContexts {
  /**
   * Dialogs
   */
  @NlsContext(prefix = "dialog.title")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, FIELD)
  annotation class DialogTitle

  @NlsContext(prefix = "dialog.message")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, LOCAL_VARIABLE)
  annotation class DialogMessage

  /**
   * Popups
   */
  @NlsContext(prefix = "popup.title")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class PopupTitle

  @NlsContext(prefix = "popup.content")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class PopupContent

  @NlsContext(prefix = "popup.advertisement")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class PopupAdvertisement

  /**
   * Notifications
   */
  @NlsContext(prefix = "notification.on.tool.window.title")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class NotificationTitle

  @NlsContext(prefix = "notification.subtitle")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class NotificationSubtitle

  @NlsContext(prefix = "notification.content")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class NotificationContent

  @NlsContext(prefix = "status.text")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class StatusText

  @NlsContext(prefix = "hint.text")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class HintText

  @NlsContext(prefix = "configurable.name")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class ConfigurableName

  @NlsContext(prefix = "parsing.error")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class ParsingError

  @NlsContext(prefix = "status.bar.text")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class StatusBarText

  /**
   * Use it for annotating OS provided notification title, such as "project built" or "tests running finished".
   * See also #SystemNotificationText.
   */
  @NlsContext(prefix = "system.notification.title")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class SystemNotificationTitle

  /**
   * Use it for annotating OS provided notification content.
   * See also #SystemNotificationTitle.
   */
  @NlsContext(prefix = "system.notification.text")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class SystemNotificationText

  @NlsContext(prefix = "command.name")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class Command

  @NlsContext(prefix = "tab.title")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, FIELD)
  annotation class TabTitle

  /**
   * Annotate by `#AttributeDescriptor` text attribute keys, see [fleet.com.intellij.openapi.options.colors.AttributesDescriptor]
   */
  @NlsContext(prefix = "attribute.descriptor")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class AttributeDescriptor

  @NlsContext(prefix = "column.name")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class ColumnName

  /**
   * Swing components
   */
  @NlsContext(prefix = "label")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class Label

  @NlsContext(prefix = "link.label")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class LinkLabel

  @NlsContext(prefix = "checkbox")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class Checkbox

  @NlsContext(prefix = "radio")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class RadioButton

  @NlsContext(prefix = "border.title")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class BorderTitle

  @NlsContext(prefix = "tooltip")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class Tooltip

  @NlsContext(prefix = "separator")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class Separator

  @NlsContext(prefix = "button")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER, FIELD)
  annotation class Button

  @NlsContext(prefix = "text")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class DetailedDescription

  @NlsContext(prefix = "list.item")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class ListItem

  @NlsContext(prefix = "progress.text")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class ProgressText

  @NlsContext(prefix = "progress.details")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class ProgressDetails

  @NlsContext(prefix = "progress.title")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class ProgressTitle

  @NlsContext(prefix = "modal.progress.title")
  @Target(TYPE, TYPE_PARAMETER, VALUE_PARAMETER, FUNCTION, PROPERTY_GETTER, PROPERTY_SETTER)
  annotation class ModalProgressTitle
}

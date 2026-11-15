package nativekotlin.irtest
import org.jetbrains.kotlin.protobuf.MessageLite
fun visitIrMessage(message: MessageLite, body: (Int, Boolean) -> Unit) {
when (message) {
is org.jetbrains.kotlin.backend.common.serialization.proto.FileEntry -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrFile -> {
if (message.hasFileEntry()) visitIrMessage(message.getFileEntry(), body)
message.getAnnotationList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.CommonIdSignature -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.AccessorIdSignature -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.FileLocalIdSignature -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.CompositeSignature -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.LocalSignature -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.FileSignature -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IdSignature -> {
if (message.hasPublicSig()) visitIrMessage(message.getPublicSig(), body)
if (message.hasPrivateSig()) visitIrMessage(message.getPrivateSig(), body)
if (message.hasAccessorSig()) visitIrMessage(message.getAccessorSig(), body)
if (message.hasCompositeSig()) visitIrMessage(message.getCompositeSig(), body)
if (message.hasLocalSig()) visitIrMessage(message.getLocalSig(), body)
if (message.hasFileSig()) visitIrMessage(message.getFileSig(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleTypeLegacy -> {
message.getAnnotationList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrSimpleType -> {
message.getAnnotationList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicType -> {
message.getAnnotationList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrDefinitelyNotNullType -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorType -> {
message.getAnnotationList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrType -> {
if (message.hasLegacySimple()) visitIrMessage(message.getLegacySimple(), body)
if (message.hasDynamic()) visitIrMessage(message.getDynamic(), body)
if (message.hasError()) visitIrMessage(message.getError(), body)
if (message.hasDnn()) visitIrMessage(message.getDnn(), body)
if (message.hasSimple()) visitIrMessage(message.getSimple(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrBreak -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrBlock -> {
message.getStatementList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrReturnableBlock -> {
if (message.hasBase()) visitIrMessage(message.getBase(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrInlinedFunctionBlock -> {
if (message.hasInlinedFunctionFileEntry()) visitIrMessage(message.getInlinedFunctionFileEntry(), body)
if (message.hasBase()) visitIrMessage(message.getBase(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.MemberAccessCommonPre_2_4_0 -> {
message.getArgumentPre240List().forEach { visitIrMessage(it, body) }
if (message.hasDispatchReceiver()) visitIrMessage(message.getDispatchReceiver(), body)
if (message.hasExtensionReceiver()) visitIrMessage(message.getExtensionReceiver(), body)
message.getRegularArgumentList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrCall -> {
if (message.hasMemberAccessPre240()) visitIrMessage(message.getMemberAccessPre240(), body)
message.getArgumentList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrConstructorCall -> {
if (message.hasMemberAccessPre240()) visitIrMessage(message.getMemberAccessPre240(), body)
message.getArgumentList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrAnnotation -> {
if (message.hasMemberAccessPre240()) visitIrMessage(message.getMemberAccessPre240(), body)
message.getArgumentList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrFunctionReference -> {
if (message.hasMemberAccessPre240()) visitIrMessage(message.getMemberAccessPre240(), body)
message.getArgumentList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrLocalDelegatedPropertyReference -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrPropertyReference -> {
if (message.hasMemberAccessPre240()) visitIrMessage(message.getMemberAccessPre240(), body)
message.getArgumentList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrRichFunctionReference -> {
message.getBoundValuesList().forEach { visitIrMessage(it, body) }
if (message.hasInvokeFunction()) visitIrMessage(message.getInvokeFunction(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrRichPropertyReference -> {
message.getBoundValuesList().forEach { visitIrMessage(it, body) }
if (message.hasGetterFunction()) visitIrMessage(message.getGetterFunction(), body)
if (message.hasSetterFunction()) visitIrMessage(message.getSetterFunction(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrComposite -> {
message.getStatementList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrClassReference -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrConst -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrContinue -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrDelegatingConstructorCall -> {
if (message.hasMemberAccessPre240()) visitIrMessage(message.getMemberAccessPre240(), body)
message.getArgumentList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrDoWhile -> {
if (message.hasLoop()) visitIrMessage(message.getLoop(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrEnumConstructorCall -> {
if (message.hasMemberAccessPre240()) visitIrMessage(message.getMemberAccessPre240(), body)
message.getArgumentList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrGetClass -> {
if (message.hasArgument()) visitIrMessage(message.getArgument(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrGetEnumValue -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon -> {
if (message.hasReceiver()) visitIrMessage(message.getReceiver(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrGetField -> {
if (message.hasFieldAccess()) visitIrMessage(message.getFieldAccess(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrGetValue -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrGetObject -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrInstanceInitializerCall -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.Loop -> {
if (message.hasCondition()) visitIrMessage(message.getCondition(), body)
if (message.hasBody()) visitIrMessage(message.getBody(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrReturn -> {
if (message.hasValue()) visitIrMessage(message.getValue(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField -> {
if (message.hasFieldAccess()) visitIrMessage(message.getFieldAccess(), body)
if (message.hasValue()) visitIrMessage(message.getValue(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrSetValue -> {
if (message.hasValue()) visitIrMessage(message.getValue(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrSpreadElement -> {
if (message.hasExpression()) visitIrMessage(message.getExpression(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrStringConcat -> {
message.getArgumentList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrThrow -> {
if (message.hasValue()) visitIrMessage(message.getValue(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrTry -> {
if (message.hasResult()) visitIrMessage(message.getResult(), body)
message.getCatchList().forEach { visitIrMessage(it, body) }
if (message.hasFinally()) visitIrMessage(message.getFinally(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrTypeOp -> {
if (message.hasArgument()) visitIrMessage(message.getArgument(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrVararg -> {
message.getElementList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrVarargElement -> {
if (message.hasExpression()) visitIrMessage(message.getExpression(), body)
if (message.hasSpreadElement()) visitIrMessage(message.getSpreadElement(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrWhen -> {
message.getBranchList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrWhile -> {
if (message.hasLoop()) visitIrMessage(message.getLoop(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrFunctionExpression -> {
if (message.hasFunction()) visitIrMessage(message.getFunction(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorExpression -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrErrorCallExpression -> {
if (message.hasReceiver()) visitIrMessage(message.getReceiver(), body)
message.getValueArgumentList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicMemberExpression -> {
if (message.hasReceiver()) visitIrMessage(message.getReceiver(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrDynamicOperatorExpression -> {
if (message.hasReceiver()) visitIrMessage(message.getReceiver(), body)
message.getArgumentList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrOperationPre_2_4_0 -> {
if (message.hasBlock()) visitIrMessage(message.getBlock(), body)
if (message.hasBreak()) visitIrMessage(message.getBreak(), body)
if (message.hasCall()) visitIrMessage(message.getCall(), body)
if (message.hasClassReference()) visitIrMessage(message.getClassReference(), body)
if (message.hasComposite()) visitIrMessage(message.getComposite(), body)
if (message.hasConst()) visitIrMessage(message.getConst(), body)
if (message.hasContinue()) visitIrMessage(message.getContinue(), body)
if (message.hasDelegatingConstructorCall()) visitIrMessage(message.getDelegatingConstructorCall(), body)
if (message.hasDoWhile()) visitIrMessage(message.getDoWhile(), body)
if (message.hasEnumConstructorCall()) visitIrMessage(message.getEnumConstructorCall(), body)
if (message.hasFunctionReference()) visitIrMessage(message.getFunctionReference(), body)
if (message.hasGetClass()) visitIrMessage(message.getGetClass(), body)
if (message.hasGetEnumValue()) visitIrMessage(message.getGetEnumValue(), body)
if (message.hasGetField()) visitIrMessage(message.getGetField(), body)
if (message.hasGetObject()) visitIrMessage(message.getGetObject(), body)
if (message.hasGetValue()) visitIrMessage(message.getGetValue(), body)
if (message.hasInstanceInitializerCall()) visitIrMessage(message.getInstanceInitializerCall(), body)
if (message.hasPropertyReference()) visitIrMessage(message.getPropertyReference(), body)
if (message.hasReturn()) visitIrMessage(message.getReturn(), body)
if (message.hasSetField()) visitIrMessage(message.getSetField(), body)
if (message.hasSetValue()) visitIrMessage(message.getSetValue(), body)
if (message.hasStringConcat()) visitIrMessage(message.getStringConcat(), body)
if (message.hasThrow()) visitIrMessage(message.getThrow(), body)
if (message.hasTry()) visitIrMessage(message.getTry(), body)
if (message.hasTypeOp()) visitIrMessage(message.getTypeOp(), body)
if (message.hasVararg()) visitIrMessage(message.getVararg(), body)
if (message.hasWhen()) visitIrMessage(message.getWhen(), body)
if (message.hasWhile()) visitIrMessage(message.getWhile(), body)
if (message.hasDynamicMember()) visitIrMessage(message.getDynamicMember(), body)
if (message.hasDynamicOperator()) visitIrMessage(message.getDynamicOperator(), body)
if (message.hasLocalDelegatedPropertyReference()) visitIrMessage(message.getLocalDelegatedPropertyReference(), body)
if (message.hasConstructorCall()) visitIrMessage(message.getConstructorCall(), body)
if (message.hasFunctionExpression()) visitIrMessage(message.getFunctionExpression(), body)
if (message.hasErrorExpression()) visitIrMessage(message.getErrorExpression(), body)
if (message.hasErrorCallExpression()) visitIrMessage(message.getErrorCallExpression(), body)
if (message.hasReturnableBlock()) visitIrMessage(message.getReturnableBlock(), body)
if (message.hasInlinedFunctionBlock()) visitIrMessage(message.getInlinedFunctionBlock(), body)
if (message.hasRichFunctionReference()) visitIrMessage(message.getRichFunctionReference(), body)
if (message.hasRichPropertyReference()) visitIrMessage(message.getRichPropertyReference(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression -> {
if (message.hasOperationPre240()) visitIrMessage(message.getOperationPre240(), body)
if (message.hasOpConst()) visitIrMessage(message.getOpConst(), body)
if (message.hasOpGetValue()) visitIrMessage(message.getOpGetValue(), body)
if (message.hasOpSetValue()) visitIrMessage(message.getOpSetValue(), body)
if (message.hasOpCall()) visitIrMessage(message.getOpCall(), body)
if (message.hasOpConstructorCall()) visitIrMessage(message.getOpConstructorCall(), body)
if (message.hasOpBlock()) visitIrMessage(message.getOpBlock(), body)
if (message.hasOpReturnableBlock()) visitIrMessage(message.getOpReturnableBlock(), body)
if (message.hasOpReturn()) visitIrMessage(message.getOpReturn(), body)
if (message.hasOpWhen()) visitIrMessage(message.getOpWhen(), body)
if (message.hasOpTypeOp()) visitIrMessage(message.getOpTypeOp(), body)
if (message.hasOpGetField()) visitIrMessage(message.getOpGetField(), body)
if (message.hasOpSetField()) visitIrMessage(message.getOpSetField(), body)
if (message.hasOpGetObject()) visitIrMessage(message.getOpGetObject(), body)
if (message.hasOpGetClass()) visitIrMessage(message.getOpGetClass(), body)
if (message.hasOpClassReference()) visitIrMessage(message.getOpClassReference(), body)
if (message.hasOpGetEnumValue()) visitIrMessage(message.getOpGetEnumValue(), body)
if (message.hasOpComposite()) visitIrMessage(message.getOpComposite(), body)
if (message.hasOpBreak()) visitIrMessage(message.getOpBreak(), body)
if (message.hasOpContinue()) visitIrMessage(message.getOpContinue(), body)
if (message.hasOpWhile()) visitIrMessage(message.getOpWhile(), body)
if (message.hasOpDoWhile()) visitIrMessage(message.getOpDoWhile(), body)
if (message.hasOpDelegatingConstructorCall()) visitIrMessage(message.getOpDelegatingConstructorCall(), body)
if (message.hasOpEnumConstructorCall()) visitIrMessage(message.getOpEnumConstructorCall(), body)
if (message.hasOpInstanceInitializerCall()) visitIrMessage(message.getOpInstanceInitializerCall(), body)
if (message.hasOpStringConcat()) visitIrMessage(message.getOpStringConcat(), body)
if (message.hasOpThrow()) visitIrMessage(message.getOpThrow(), body)
if (message.hasOpTry()) visitIrMessage(message.getOpTry(), body)
if (message.hasOpVararg()) visitIrMessage(message.getOpVararg(), body)
if (message.hasOpDynamicMember()) visitIrMessage(message.getOpDynamicMember(), body)
if (message.hasOpDynamicOperator()) visitIrMessage(message.getOpDynamicOperator(), body)
if (message.hasOpLocalDelegatedPropertyReference()) visitIrMessage(message.getOpLocalDelegatedPropertyReference(), body)
if (message.hasOpFunctionExpression()) visitIrMessage(message.getOpFunctionExpression(), body)
if (message.hasOpErrorExpression()) visitIrMessage(message.getOpErrorExpression(), body)
if (message.hasOpErrorCallExpression()) visitIrMessage(message.getOpErrorCallExpression(), body)
if (message.hasOpFunctionReference()) visitIrMessage(message.getOpFunctionReference(), body)
if (message.hasOpPropertyReference()) visitIrMessage(message.getOpPropertyReference(), body)
if (message.hasOpRichFunctionReference()) visitIrMessage(message.getOpRichFunctionReference(), body)
if (message.hasOpRichPropertyReference()) visitIrMessage(message.getOpRichPropertyReference(), body)
if (message.hasOpInlinedFunctionBlock()) visitIrMessage(message.getOpInlinedFunctionBlock(), body)
if (message.hasOpMissingExpression()) visitIrMessage(message.getOpMissingExpression(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.NullableIrExpression -> {
if (message.hasExpression()) visitIrMessage(message.getExpression(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrMissingExpression -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrDeclarationBase -> {
message.getAnnotationList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrFunctionBase -> {
if (message.hasBase()) visitIrMessage(message.getBase(), body)
message.getTypeParameterList().forEach { visitIrMessage(it, body) }
if (message.hasDispatchReceiver()) visitIrMessage(message.getDispatchReceiver(), body)
message.getContextParameterList().forEach { visitIrMessage(it, body) }
if (message.hasExtensionReceiver()) visitIrMessage(message.getExtensionReceiver(), body)
message.getRegularParameterList().forEach { visitIrMessage(it, body) }
if (message.hasBody()) body(message.getBody(), false)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrFunction -> {
if (message.hasBase()) visitIrMessage(message.getBase(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrConstructor -> {
if (message.hasBase()) visitIrMessage(message.getBase(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrField -> {
if (message.hasBase()) visitIrMessage(message.getBase(), body)
if (message.hasInitializer()) body(message.getInitializer(), true)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrLocalDelegatedProperty -> {
if (message.hasBase()) visitIrMessage(message.getBase(), body)
if (message.hasDelegate()) visitIrMessage(message.getDelegate(), body)
if (message.hasGetter()) visitIrMessage(message.getGetter(), body)
if (message.hasSetter()) visitIrMessage(message.getSetter(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrProperty -> {
if (message.hasBase()) visitIrMessage(message.getBase(), body)
if (message.hasBackingField()) visitIrMessage(message.getBackingField(), body)
if (message.hasGetter()) visitIrMessage(message.getGetter(), body)
if (message.hasSetter()) visitIrMessage(message.getSetter(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrVariable -> {
if (message.hasBase()) visitIrMessage(message.getBase(), body)
if (message.hasInitializer()) visitIrMessage(message.getInitializer(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrValueParameter -> {
if (message.hasBase()) visitIrMessage(message.getBase(), body)
if (message.hasDefaultValue()) body(message.getDefaultValue(), true)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrTypeParameter -> {
if (message.hasBase()) visitIrMessage(message.getBase(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrClass -> {
if (message.hasBase()) visitIrMessage(message.getBase(), body)
if (message.hasThisReceiver()) visitIrMessage(message.getThisReceiver(), body)
message.getTypeParameterList().forEach { visitIrMessage(it, body) }
message.getDeclarationList().forEach { visitIrMessage(it, body) }
if (message.hasInlineClassRepresentation()) visitIrMessage(message.getInlineClassRepresentation(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrTypeAlias -> {
if (message.hasBase()) visitIrMessage(message.getBase(), body)
message.getTypeParameterList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrEnumEntry -> {
if (message.hasBase()) visitIrMessage(message.getBase(), body)
if (message.hasInitializer()) body(message.getInitializer(), true)
if (message.hasCorrespondingClass()) visitIrMessage(message.getCorrespondingClass(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrAnonymousInit -> {
if (message.hasBase()) visitIrMessage(message.getBase(), body)
if (message.hasBody()) body(message.getBody(), false)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrDeclaration -> {
if (message.hasIrAnonymousInit()) visitIrMessage(message.getIrAnonymousInit(), body)
if (message.hasIrClass()) visitIrMessage(message.getIrClass(), body)
if (message.hasIrConstructor()) visitIrMessage(message.getIrConstructor(), body)
if (message.hasIrEnumEntry()) visitIrMessage(message.getIrEnumEntry(), body)
if (message.hasIrField()) visitIrMessage(message.getIrField(), body)
if (message.hasIrFunction()) visitIrMessage(message.getIrFunction(), body)
if (message.hasIrProperty()) visitIrMessage(message.getIrProperty(), body)
if (message.hasIrTypeParameter()) visitIrMessage(message.getIrTypeParameter(), body)
if (message.hasIrVariable()) visitIrMessage(message.getIrVariable(), body)
if (message.hasIrValueParameter()) visitIrMessage(message.getIrValueParameter(), body)
if (message.hasIrLocalDelegatedProperty()) visitIrMessage(message.getIrLocalDelegatedProperty(), body)
if (message.hasIrTypeAlias()) visitIrMessage(message.getIrTypeAlias(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrBranch -> {
if (message.hasCondition()) visitIrMessage(message.getCondition(), body)
if (message.hasResult()) visitIrMessage(message.getResult(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrBlockBody -> {
message.getStatementList().forEach { visitIrMessage(it, body) }
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrCatch -> {
if (message.hasCatchParameter()) visitIrMessage(message.getCatchParameter(), body)
if (message.hasResult()) visitIrMessage(message.getResult(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrSyntheticBody -> {
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrStatement -> {
if (message.hasDeclaration()) visitIrMessage(message.getDeclaration(), body)
if (message.hasExpression()) visitIrMessage(message.getExpression(), body)
if (message.hasBlockBody()) visitIrMessage(message.getBlockBody(), body)
if (message.hasBranch()) visitIrMessage(message.getBranch(), body)
if (message.hasCatch()) visitIrMessage(message.getCatch(), body)
if (message.hasSyntheticBody()) visitIrMessage(message.getSyntheticBody(), body)
}
is org.jetbrains.kotlin.backend.common.serialization.proto.IrInlineClassRepresentation -> {
}
else -> error("Not a Kotlin IR message")
}}

/*
 ** Copyright 2011, The Android Open Source Project
 **
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

// auto generated by generate_MessageParser_java.py,
//  which also prints skeleton code for MessageParserEx.java

package com.android.ide.eclipse.gldebugger;

import com.android.ide.eclipse.gldebugger.DebuggerMessage.Message;
import com.android.ide.eclipse.gldebugger.DebuggerMessage.Message.Function;
import com.google.protobuf.ByteString;

import java.nio.ByteBuffer;

public abstract class MessageParser {

    String args;

    String[] getList()
    {
        String arg = args;
        args = args.substring(args.lastIndexOf('}') + 1);
        final int comma = args.indexOf(',');
        if (comma >= 0)
            args = args.substring(comma + 1).trim();
        else
            args = null;

        final int comment = arg.indexOf('=');
        if (comment >= 0)
            arg = arg.substring(comment + 1);
        arg = arg.trim();
        assert arg.charAt(0) == '{';
        arg = arg.substring(1, arg.lastIndexOf('}')).trim();
        return arg.split("\\s*,\\s*");
    }

    ByteString parseFloats(int count) {
        ByteBuffer buffer = ByteBuffer.allocate(count * 4);
        buffer.order(GLFramesView.TARGET_BYTE_ORDER);
        String [] arg = getList();
        for (int i = 0; i < count; i++)
            buffer.putFloat(Float.parseFloat(arg[i].trim()));
        buffer.rewind();
        return ByteString.copyFrom(buffer);
    }

    ByteString parseInts(int count) {
        ByteBuffer buffer = ByteBuffer.allocate(count * 4);
        buffer.order(GLFramesView.TARGET_BYTE_ORDER);
        String [] arg = getList();
        for (int i = 0; i < count; i++)
            buffer.putInt(Integer.parseInt(arg[i].trim()));
        buffer.rewind();
        return ByteString.copyFrom(buffer);
    }

    ByteString parseUInts(int count) {
        ByteBuffer buffer = ByteBuffer.allocate(count * 4);
        buffer.order(GLFramesView.TARGET_BYTE_ORDER);
        String [] arg = getList();
        for (int i = 0; i < count; i++)
            buffer.putInt((int)(Long.parseLong(arg[i].trim()) & 0xffffffff));
        buffer.rewind();
        return ByteString.copyFrom(buffer);
    }

    ByteString parseMatrix(int columns, int count) {
        return parseFloats(columns * columns * count);
    }

    ByteString parseString() {
        // TODO: escape sequence and proper string literal
        String arg = args.substring(args.indexOf('"') + 1, args.lastIndexOf('"'));
        args = args.substring(args.lastIndexOf('"'));
        int comma = args.indexOf(',');
        if (comma >= 0)
            args = args.substring(comma + 1).trim();
        else
            args = null;
        return ByteString.copyFromUtf8(arg);
    }

    String getArgument()
    {
        final int comma = args.indexOf(',');
        String arg = null;
        if (comma >= 0)
        {
            arg = args.substring(0, comma);
            args = args.substring(comma + 1);
        }
        else
        {
            arg = args;
            args = null;
        }
        final int comment = arg.indexOf('=');
        if (comment >= 0)
            arg = arg.substring(comment + 1);
        return arg.trim();
    }

    int parseArgument()
    {
        String arg = getArgument();
        if (arg.startsWith("GL_"))
            return GLEnum.valueOf(arg).value;
        else if (arg.toLowerCase().startsWith("0x"))
            return Integer.parseInt(arg.substring(2), 16);
        else
            return Integer.parseInt(arg);
    }

    int parseFloat()
    {
        String arg = getArgument();
        return Float.floatToRawIntBits(Float.parseFloat(arg));
    }

    public void parse(final Message.Builder builder, String string) {
        int lparen = string.indexOf("("), rparen = string.lastIndexOf(")");
        String s = string.substring(0, lparen).trim();
        args = string.substring(lparen + 1, rparen);
        String[] t = s.split(" ");
        Function function = Function.valueOf(t[t.length - 1]);
        builder.setFunction(function);
        switch (function) {
            case glActiveTexture:
                builder.setArg0(parseArgument()); // GLenum texture
                break;
            case glAttachShader:
                builder.setArg0(parseArgument()); // GLuint program
                builder.setArg1(parseArgument()); // GLuint shader
                break;
            case glBindAttribLocation:
                builder.setArg0(parseArgument()); // GLuint program
                builder.setArg1(parseArgument()); // GLuint index
                builder.setData(parseString()); // GLchar name
                break;
            case glBindBuffer:
                builder.setArg0(parseArgument()); // GLenum target
                builder.setArg1(parseArgument()); // GLuint buffer
                break;
            case glBindFramebuffer:
                builder.setArg0(parseArgument()); // GLenum target
                builder.setArg1(parseArgument()); // GLuint framebuffer
                break;
            case glBindRenderbuffer:
                builder.setArg0(parseArgument()); // GLenum target
                builder.setArg1(parseArgument()); // GLuint renderbuffer
                break;
            case glBindTexture:
                builder.setArg0(parseArgument()); // GLenum target
                builder.setArg1(parseArgument()); // GLuint texture
                break;
            case glBlendColor:
                builder.setArg0(parseFloat()); // GLclampf red
                builder.setArg1(parseFloat()); // GLclampf green
                builder.setArg2(parseFloat()); // GLclampf blue
                builder.setArg3(parseFloat()); // GLclampf alpha
                break;
            case glBlendEquation:
                builder.setArg0(parseArgument()); // GLenum mode
                break;
            case glBlendEquationSeparate:
                builder.setArg0(parseArgument()); // GLenum modeRGB
                builder.setArg1(parseArgument()); // GLenum modeAlpha
                break;
            case glBlendFunc:
                builder.setArg0(parseArgument()); // GLenum sfactor
                builder.setArg1(parseArgument()); // GLenum dfactor
                break;
            case glBlendFuncSeparate:
                builder.setArg0(parseArgument()); // GLenum srcRGB
                builder.setArg1(parseArgument()); // GLenum dstRGB
                builder.setArg2(parseArgument()); // GLenum srcAlpha
                builder.setArg3(parseArgument()); // GLenum dstAlpha
                break;
            case glBufferData:
                parse_glBufferData(builder);
                break;
            case glBufferSubData:
                parse_glBufferSubData(builder);
                break;
            case glCheckFramebufferStatus:
                builder.setArg0(parseArgument()); // GLenum target
                break;
            case glClear:
                builder.setArg0(parseArgument()); // GLbitfield mask
                break;
            case glClearColor:
                builder.setArg0(parseFloat()); // GLclampf red
                builder.setArg1(parseFloat()); // GLclampf green
                builder.setArg2(parseFloat()); // GLclampf blue
                builder.setArg3(parseFloat()); // GLclampf alpha
                break;
            case glClearDepthf:
                builder.setArg0(parseFloat()); // GLclampf depth
                break;
            case glClearStencil:
                builder.setArg0(parseArgument()); // GLint s
                break;
            case glColorMask:
                builder.setArg0(parseArgument()); // GLboolean red
                builder.setArg1(parseArgument()); // GLboolean green
                builder.setArg2(parseArgument()); // GLboolean blue
                builder.setArg3(parseArgument()); // GLboolean alpha
                break;
            case glCompileShader:
                builder.setArg0(parseArgument()); // GLuint shader
                break;
            case glCompressedTexImage2D:
                parse_glCompressedTexImage2D(builder);
                break;
            case glCompressedTexSubImage2D:
                parse_glCompressedTexSubImage2D(builder);
                break;
            case glCopyTexImage2D:
                builder.setArg0(parseArgument()); // GLenum target
                builder.setArg1(parseArgument()); // GLint level
                builder.setArg2(parseArgument()); // GLenum internalformat
                builder.setArg3(parseArgument()); // GLint x
                builder.setArg4(parseArgument()); // GLint y
                builder.setArg5(parseArgument()); // GLsizei width
                builder.setArg6(parseArgument()); // GLsizei height
                builder.setArg7(parseArgument()); // GLint border
                break;
            case glCopyTexSubImage2D:
                builder.setArg0(parseArgument()); // GLenum target
                builder.setArg1(parseArgument()); // GLint level
                builder.setArg2(parseArgument()); // GLint xoffset
                builder.setArg3(parseArgument()); // GLint yoffset
                builder.setArg4(parseArgument()); // GLint x
                builder.setArg5(parseArgument()); // GLint y
                builder.setArg6(parseArgument()); // GLsizei width
                builder.setArg7(parseArgument()); // GLsizei height
                break;
            case glCreateProgram:
                break;
            case glCreateShader:
                builder.setArg0(parseArgument()); // GLenum type
                break;
            case glCullFace:
                builder.setArg0(parseArgument()); // GLenum mode
                break;
            case glDeleteBuffers:
                builder.setArg0(parseArgument()); // GLsizei n
                builder.setData(parseUInts(1 * builder.getArg0())); // GLuint buffers
                break;
            case glDeleteFramebuffers:
                builder.setArg0(parseArgument()); // GLsizei n
                builder.setData(parseUInts(1 * builder.getArg0())); // GLuint framebuffers
                break;
            case glDeleteProgram:
                builder.setArg0(parseArgument()); // GLuint program
                break;
            case glDeleteRenderbuffers:
                builder.setArg0(parseArgument()); // GLsizei n
                builder.setData(parseUInts(1 * builder.getArg0())); // GLuint renderbuffers
                break;
            case glDeleteShader:
                builder.setArg0(parseArgument()); // GLuint shader
                break;
            case glDeleteTextures:
                builder.setArg0(parseArgument()); // GLsizei n
                builder.setData(parseUInts(1 * builder.getArg0())); // GLuint textures
                break;
            case glDepthFunc:
                builder.setArg0(parseArgument()); // GLenum func
                break;
            case glDepthMask:
                builder.setArg0(parseArgument()); // GLboolean flag
                break;
            case glDepthRangef:
                builder.setArg0(parseFloat()); // GLclampf zNear
                builder.setArg1(parseFloat()); // GLclampf zFar
                break;
            case glDetachShader:
                builder.setArg0(parseArgument()); // GLuint program
                builder.setArg1(parseArgument()); // GLuint shader
                break;
            case glDisable:
                builder.setArg0(parseArgument()); // GLenum cap
                break;
            case glDisableVertexAttribArray:
                builder.setArg0(parseArgument()); // GLuint index
                break;
            case glDrawArrays:
                builder.setArg0(parseArgument()); // GLenum mode
                builder.setArg1(parseArgument()); // GLint first
                builder.setArg2(parseArgument()); // GLsizei count
                break;
            case glDrawElements:
                parse_glDrawElements(builder);
                break;
            case glEnable:
                builder.setArg0(parseArgument()); // GLenum cap
                break;
            case glEnableVertexAttribArray:
                builder.setArg0(parseArgument()); // GLuint index
                break;
            case glFinish:
                break;
            case glFlush:
                break;
            case glFramebufferRenderbuffer:
                builder.setArg0(parseArgument()); // GLenum target
                builder.setArg1(parseArgument()); // GLenum attachment
                builder.setArg2(parseArgument()); // GLenum renderbuffertarget
                builder.setArg3(parseArgument()); // GLuint renderbuffer
                break;
            case glFramebufferTexture2D:
                builder.setArg0(parseArgument()); // GLenum target
                builder.setArg1(parseArgument()); // GLenum attachment
                builder.setArg2(parseArgument()); // GLenum textarget
                builder.setArg3(parseArgument()); // GLuint texture
                builder.setArg4(parseArgument()); // GLint level
                break;
            case glFrontFace:
                builder.setArg0(parseArgument()); // GLenum mode
                break;
            case glGenBuffers:
                builder.setArg0(parseArgument()); // GLsizei n
                builder.setData(parseUInts(1 * builder.getArg0())); // GLuint buffers
                break;
            case glGenerateMipmap:
                builder.setArg0(parseArgument()); // GLenum target
                break;
            case glGenFramebuffers:
                builder.setArg0(parseArgument()); // GLsizei n
                builder.setData(parseUInts(1 * builder.getArg0())); // GLuint framebuffers
                break;
            case glGenRenderbuffers:
                builder.setArg0(parseArgument()); // GLsizei n
                builder.setData(parseUInts(1 * builder.getArg0())); // GLuint renderbuffers
                break;
            case glGenTextures:
                builder.setArg0(parseArgument()); // GLsizei n
                builder.setData(parseUInts(1 * builder.getArg0())); // GLuint textures
                break;
            case glGetActiveAttrib:
                parse_glGetActiveAttrib(builder);
                break;
            case glGetActiveUniform:
                parse_glGetActiveUniform(builder);
                break;
            case glGetAttachedShaders:
                parse_glGetAttachedShaders(builder);
                break;
            case glGetAttribLocation:
                builder.setArg0(parseArgument()); // GLuint program
                builder.setData(parseString()); // GLchar name
                break;
            case glGetBooleanv:
                parse_glGetBooleanv(builder);
                break;
            case glGetBufferParameteriv:
                parse_glGetBufferParameteriv(builder);
                break;
            case glGetError:
                break;
            case glGetFloatv:
                parse_glGetFloatv(builder);
                break;
            case glGetFramebufferAttachmentParameteriv:
                parse_glGetFramebufferAttachmentParameteriv(builder);
                break;
            case glGetIntegerv:
                parse_glGetIntegerv(builder);
                break;
            case glGetProgramiv:
                builder.setArg0(parseArgument()); // GLuint program
                builder.setArg1(parseArgument()); // GLenum pname
                builder.setData(parseInts(1)); // GLint params
                break;
            case glGetProgramInfoLog:
                parse_glGetProgramInfoLog(builder);
                break;
            case glGetRenderbufferParameteriv:
                parse_glGetRenderbufferParameteriv(builder);
                break;
            case glGetShaderiv:
                builder.setArg0(parseArgument()); // GLuint shader
                builder.setArg1(parseArgument()); // GLenum pname
                builder.setData(parseInts(1)); // GLint params
                break;
            case glGetShaderInfoLog:
                parse_glGetShaderInfoLog(builder);
                break;
            case glGetShaderPrecisionFormat:
                parse_glGetShaderPrecisionFormat(builder);
                break;
            case glGetShaderSource:
                parse_glGetShaderSource(builder);
                break;
            case glGetString:
                builder.setArg0(parseArgument()); // GLenum name
                break;
            case glGetTexParameterfv:
                parse_glGetTexParameterfv(builder);
                break;
            case glGetTexParameteriv:
                parse_glGetTexParameteriv(builder);
                break;
            case glGetUniformfv:
                parse_glGetUniformfv(builder);
                break;
            case glGetUniformiv:
                parse_glGetUniformiv(builder);
                break;
            case glGetUniformLocation:
                builder.setArg0(parseArgument()); // GLuint program
                builder.setData(parseString()); // GLchar name
                break;
            case glGetVertexAttribfv:
                parse_glGetVertexAttribfv(builder);
                break;
            case glGetVertexAttribiv:
                parse_glGetVertexAttribiv(builder);
                break;
            case glGetVertexAttribPointerv:
                parse_glGetVertexAttribPointerv(builder);
                break;
            case glHint:
                builder.setArg0(parseArgument()); // GLenum target
                builder.setArg1(parseArgument()); // GLenum mode
                break;
            case glIsBuffer:
                builder.setArg0(parseArgument()); // GLuint buffer
                break;
            case glIsEnabled:
                builder.setArg0(parseArgument()); // GLenum cap
                break;
            case glIsFramebuffer:
                builder.setArg0(parseArgument()); // GLuint framebuffer
                break;
            case glIsProgram:
                builder.setArg0(parseArgument()); // GLuint program
                break;
            case glIsRenderbuffer:
                builder.setArg0(parseArgument()); // GLuint renderbuffer
                break;
            case glIsShader:
                builder.setArg0(parseArgument()); // GLuint shader
                break;
            case glIsTexture:
                builder.setArg0(parseArgument()); // GLuint texture
                break;
            case glLineWidth:
                builder.setArg0(parseFloat()); // GLfloat width
                break;
            case glLinkProgram:
                builder.setArg0(parseArgument()); // GLuint program
                break;
            case glPixelStorei:
                builder.setArg0(parseArgument()); // GLenum pname
                builder.setArg1(parseArgument()); // GLint param
                break;
            case glPolygonOffset:
                builder.setArg0(parseFloat()); // GLfloat factor
                builder.setArg1(parseFloat()); // GLfloat units
                break;
            case glReadPixels:
                parse_glReadPixels(builder);
                break;
            case glReleaseShaderCompiler:
                break;
            case glRenderbufferStorage:
                builder.setArg0(parseArgument()); // GLenum target
                builder.setArg1(parseArgument()); // GLenum internalformat
                builder.setArg2(parseArgument()); // GLsizei width
                builder.setArg3(parseArgument()); // GLsizei height
                break;
            case glSampleCoverage:
                builder.setArg0(parseFloat()); // GLclampf value
                builder.setArg1(parseArgument()); // GLboolean invert
                break;
            case glScissor:
                builder.setArg0(parseArgument()); // GLint x
                builder.setArg1(parseArgument()); // GLint y
                builder.setArg2(parseArgument()); // GLsizei width
                builder.setArg3(parseArgument()); // GLsizei height
                break;
            case glShaderBinary:
                parse_glShaderBinary(builder);
                break;
            case glShaderSource:
                parse_glShaderSource(builder);
                break;
            case glStencilFunc:
                builder.setArg0(parseArgument()); // GLenum func
                builder.setArg1(parseArgument()); // GLint ref
                builder.setArg2(parseArgument()); // GLuint mask
                break;
            case glStencilFuncSeparate:
                builder.setArg0(parseArgument()); // GLenum face
                builder.setArg1(parseArgument()); // GLenum func
                builder.setArg2(parseArgument()); // GLint ref
                builder.setArg3(parseArgument()); // GLuint mask
                break;
            case glStencilMask:
                builder.setArg0(parseArgument()); // GLuint mask
                break;
            case glStencilMaskSeparate:
                builder.setArg0(parseArgument()); // GLenum face
                builder.setArg1(parseArgument()); // GLuint mask
                break;
            case glStencilOp:
                builder.setArg0(parseArgument()); // GLenum fail
                builder.setArg1(parseArgument()); // GLenum zfail
                builder.setArg2(parseArgument()); // GLenum zpass
                break;
            case glStencilOpSeparate:
                builder.setArg0(parseArgument()); // GLenum face
                builder.setArg1(parseArgument()); // GLenum fail
                builder.setArg2(parseArgument()); // GLenum zfail
                builder.setArg3(parseArgument()); // GLenum zpass
                break;
            case glTexImage2D:
                parse_glTexImage2D(builder);
                break;
            case glTexParameterf:
                builder.setArg0(parseArgument()); // GLenum target
                builder.setArg1(parseArgument()); // GLenum pname
                builder.setArg2(parseFloat()); // GLfloat param
                break;
            case glTexParameterfv:
                parse_glTexParameterfv(builder);
                break;
            case glTexParameteri:
                builder.setArg0(parseArgument()); // GLenum target
                builder.setArg1(parseArgument()); // GLenum pname
                builder.setArg2(parseArgument()); // GLint param
                break;
            case glTexParameteriv:
                parse_glTexParameteriv(builder);
                break;
            case glTexSubImage2D:
                parse_glTexSubImage2D(builder);
                break;
            case glUniform1f:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseFloat()); // GLfloat x
                break;
            case glUniform1fv:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseArgument()); // GLsizei count
                builder.setData(parseFloats(1 * builder.getArg1())); // GLfloat v
                break;
            case glUniform1i:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseArgument()); // GLint x
                break;
            case glUniform1iv:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseArgument()); // GLsizei count
                builder.setData(parseInts(1 * builder.getArg1())); // GLint v
                break;
            case glUniform2f:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseFloat()); // GLfloat x
                builder.setArg2(parseFloat()); // GLfloat y
                break;
            case glUniform2fv:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseArgument()); // GLsizei count
                builder.setData(parseFloats(2 * builder.getArg1())); // GLfloat v
                break;
            case glUniform2i:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseArgument()); // GLint x
                builder.setArg2(parseArgument()); // GLint y
                break;
            case glUniform2iv:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseArgument()); // GLsizei count
                builder.setData(parseInts(2 * builder.getArg1())); // GLint v
                break;
            case glUniform3f:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseFloat()); // GLfloat x
                builder.setArg2(parseFloat()); // GLfloat y
                builder.setArg3(parseFloat()); // GLfloat z
                break;
            case glUniform3fv:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseArgument()); // GLsizei count
                builder.setData(parseFloats(3 * builder.getArg1())); // GLfloat v
                break;
            case glUniform3i:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseArgument()); // GLint x
                builder.setArg2(parseArgument()); // GLint y
                builder.setArg3(parseArgument()); // GLint z
                break;
            case glUniform3iv:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseArgument()); // GLsizei count
                builder.setData(parseInts(3 * builder.getArg1())); // GLint v
                break;
            case glUniform4f:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseFloat()); // GLfloat x
                builder.setArg2(parseFloat()); // GLfloat y
                builder.setArg3(parseFloat()); // GLfloat z
                builder.setArg4(parseFloat()); // GLfloat w
                break;
            case glUniform4fv:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseArgument()); // GLsizei count
                builder.setData(parseFloats(4 * builder.getArg1())); // GLfloat v
                break;
            case glUniform4i:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseArgument()); // GLint x
                builder.setArg2(parseArgument()); // GLint y
                builder.setArg3(parseArgument()); // GLint z
                builder.setArg4(parseArgument()); // GLint w
                break;
            case glUniform4iv:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseArgument()); // GLsizei count
                builder.setData(parseInts(4 * builder.getArg1())); // GLint v
                break;
            case glUniformMatrix2fv:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseArgument()); // GLsizei count
                builder.setArg2(parseArgument()); // GLboolean transpose
                builder.setData(parseMatrix(2, builder.getArg1())); // GLfloat value
                break;
            case glUniformMatrix3fv:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseArgument()); // GLsizei count
                builder.setArg2(parseArgument()); // GLboolean transpose
                builder.setData(parseMatrix(3, builder.getArg1())); // GLfloat value
                break;
            case glUniformMatrix4fv:
                builder.setArg0(parseArgument()); // GLint location
                builder.setArg1(parseArgument()); // GLsizei count
                builder.setArg2(parseArgument()); // GLboolean transpose
                builder.setData(parseMatrix(4, builder.getArg1())); // GLfloat value
                break;
            case glUseProgram:
                builder.setArg0(parseArgument()); // GLuint program
                break;
            case glValidateProgram:
                builder.setArg0(parseArgument()); // GLuint program
                break;
            case glVertexAttrib1f:
                builder.setArg0(parseArgument()); // GLuint indx
                builder.setArg1(parseFloat()); // GLfloat x
                break;
            case glVertexAttrib1fv:
                builder.setArg0(parseArgument()); // GLuint indx
                builder.setData(parseFloats(1)); // GLfloat values
                break;
            case glVertexAttrib2f:
                builder.setArg0(parseArgument()); // GLuint indx
                builder.setArg1(parseFloat()); // GLfloat x
                builder.setArg2(parseFloat()); // GLfloat y
                break;
            case glVertexAttrib2fv:
                builder.setArg0(parseArgument()); // GLuint indx
                builder.setData(parseFloats(2)); // GLfloat values
                break;
            case glVertexAttrib3f:
                builder.setArg0(parseArgument()); // GLuint indx
                builder.setArg1(parseFloat()); // GLfloat x
                builder.setArg2(parseFloat()); // GLfloat y
                builder.setArg3(parseFloat()); // GLfloat z
                break;
            case glVertexAttrib3fv:
                builder.setArg0(parseArgument()); // GLuint indx
                builder.setData(parseFloats(3)); // GLfloat values
                break;
            case glVertexAttrib4f:
                builder.setArg0(parseArgument()); // GLuint indx
                builder.setArg1(parseFloat()); // GLfloat x
                builder.setArg2(parseFloat()); // GLfloat y
                builder.setArg3(parseFloat()); // GLfloat z
                builder.setArg4(parseFloat()); // GLfloat w
                break;
            case glVertexAttrib4fv:
                builder.setArg0(parseArgument()); // GLuint indx
                builder.setData(parseFloats(4)); // GLfloat values
                break;
            case glVertexAttribPointer:
                parse_glVertexAttribPointer(builder);
                break;
            case glViewport:
                builder.setArg0(parseArgument()); // GLint x
                builder.setArg1(parseArgument()); // GLint y
                builder.setArg2(parseArgument()); // GLsizei width
                builder.setArg3(parseArgument()); // GLsizei height
                break;
            default:
                assert false;
        }
    }
    abstract void parse_glBufferData(Message.Builder builder);
    abstract void parse_glBufferSubData(Message.Builder builder);
    abstract void parse_glCompressedTexImage2D(Message.Builder builder);
    abstract void parse_glCompressedTexSubImage2D(Message.Builder builder);
    abstract void parse_glDrawElements(Message.Builder builder);
    abstract void parse_glGetActiveAttrib(Message.Builder builder);
    abstract void parse_glGetActiveUniform(Message.Builder builder);
    abstract void parse_glGetAttachedShaders(Message.Builder builder);
    abstract void parse_glGetBooleanv(Message.Builder builder);
    abstract void parse_glGetBufferParameteriv(Message.Builder builder);
    abstract void parse_glGetFloatv(Message.Builder builder);
    abstract void parse_glGetFramebufferAttachmentParameteriv(Message.Builder builder);
    abstract void parse_glGetIntegerv(Message.Builder builder);
    abstract void parse_glGetProgramInfoLog(Message.Builder builder);
    abstract void parse_glGetRenderbufferParameteriv(Message.Builder builder);
    abstract void parse_glGetShaderInfoLog(Message.Builder builder);
    abstract void parse_glGetShaderPrecisionFormat(Message.Builder builder);
    abstract void parse_glGetShaderSource(Message.Builder builder);
    abstract void parse_glGetTexParameterfv(Message.Builder builder);
    abstract void parse_glGetTexParameteriv(Message.Builder builder);
    abstract void parse_glGetUniformfv(Message.Builder builder);
    abstract void parse_glGetUniformiv(Message.Builder builder);
    abstract void parse_glGetVertexAttribfv(Message.Builder builder);
    abstract void parse_glGetVertexAttribiv(Message.Builder builder);
    abstract void parse_glGetVertexAttribPointerv(Message.Builder builder);
    abstract void parse_glReadPixels(Message.Builder builder);
    abstract void parse_glShaderBinary(Message.Builder builder);
    abstract void parse_glShaderSource(Message.Builder builder);
    abstract void parse_glTexImage2D(Message.Builder builder);
    abstract void parse_glTexParameterfv(Message.Builder builder);
    abstract void parse_glTexParameteriv(Message.Builder builder);
    abstract void parse_glTexSubImage2D(Message.Builder builder);
    abstract void parse_glVertexAttribPointer(Message.Builder builder);
}

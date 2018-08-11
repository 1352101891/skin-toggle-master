package com.skinlibrary.callback;

import com.skinlibrary.entity.SourceBo;

public interface SkinLoadCallback  {

    void preDo();
    void progressDo(int percent);
    void endDo(SourceBo bo);
}

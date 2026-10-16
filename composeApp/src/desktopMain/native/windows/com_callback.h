#pragma once

// MinGW's WRL implements ComPtr, but not WRL::Callback. Keep the same COM
// ownership semantics when building the bridge with the ARM64 LLVM toolchain.
#ifdef __MINGW32__
#include <atomic>
#include <new>
#include <utility>
#include <wrl/client.h>

// The Microsoft SDK has MIDL UUID attributes; MinGW uses these declarations
// for __uuidof instead. These are the SDK's stable COM interface identifiers.
__CRT_UUID_DECL(ICoreWebView2CreateCoreWebView2EnvironmentCompletedHandler, 0x4e8a3389, 0xc9d8, 0x4bd2, 0xb6, 0xb5, 0x12, 0x4f, 0xee, 0x6c, 0xc1, 0x4d)
__CRT_UUID_DECL(ICoreWebView2CreateCoreWebView2ControllerCompletedHandler, 0x6c4819f3, 0xc9b7, 0x4260, 0x81, 0x27, 0xc9, 0xf5, 0xbd, 0xe7, 0xf6, 0x8c)
__CRT_UUID_DECL(ICoreWebView2Controller2, 0xc979903e, 0xd4ca, 0x4228, 0x92, 0xeb, 0x47, 0xee, 0x3f, 0xa9, 0x6e, 0xab)
__CRT_UUID_DECL(ICoreWebView2AcceleratorKeyPressedEventHandler, 0xb29c7e28, 0xfa79, 0x41a8, 0x8e, 0x44, 0x65, 0x81, 0x1c, 0x76, 0xdc, 0xb2)
__CRT_UUID_DECL(ICoreWebView2WebMessageReceivedEventHandler, 0x57213f19, 0x00e6, 0x49fa, 0x8e, 0x07, 0x89, 0x8e, 0xa0, 0x1e, 0xcb, 0xd2)
__CRT_UUID_DECL(ICoreWebView2NavigationCompletedEventHandler, 0xd33a35bf, 0x1c49, 0x4f98, 0x93, 0xab, 0x00, 0x6e, 0x05, 0x33, 0xfe, 0x1c)

namespace nuvio {
template <typename Interface, typename Function, typename Signature>
class ComCallback;

template <typename Interface, typename Function, typename... Args>
class ComCallback<Interface, Function, HRESULT (STDMETHODCALLTYPE Interface::*)(Args...)>
    final : public Interface {
public:
    explicit ComCallback(Function function) : function_(std::move(function)) {}

    HRESULT STDMETHODCALLTYPE QueryInterface(REFIID iid, void **object) override {
        if (!object) return E_POINTER;
        *object = nullptr;
        if (IsEqualIID(iid, __uuidof(IUnknown)) || IsEqualIID(iid, __uuidof(Interface))) {
            *object = static_cast<Interface *>(this);
            AddRef();
            return S_OK;
        }
        return E_NOINTERFACE;
    }
    ULONG STDMETHODCALLTYPE AddRef() override { return ++references_; }
    ULONG STDMETHODCALLTYPE Release() override {
        const ULONG count = --references_;
        if (count == 0) delete this;
        return count;
    }
    HRESULT STDMETHODCALLTYPE Invoke(Args... args) override {
        // C++ exceptions must not escape a COM boundary.
        try {
            return function_(args...);
        } catch (const std::bad_alloc &) {
            return E_OUTOFMEMORY;
        } catch (...) {
            return E_FAIL;
        }
    }

private:
    std::atomic<ULONG> references_{1};
    Function function_;
};

template <typename Interface, typename Function>
Microsoft::WRL::ComPtr<Interface> Callback(Function function) {
    Microsoft::WRL::ComPtr<Interface> result;
    result.Attach(new ComCallback<Interface, Function, decltype(&Interface::Invoke)>(std::move(function)));
    return result;
}
} // namespace nuvio
using nuvio::Callback;
#else
using Microsoft::WRL::Callback;
#endif
